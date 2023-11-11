package example.infrastructure.query.takingrecord

import example.application.query.shared.type.*
import example.application.query.takingrecord.*
import example.application.service.takingrecord.*
import example.application.shared.usersession.*
import example.domain.model.account.profile.*
import example.domain.model.account.profile.profileimage.*
import example.domain.model.medicine.*
import example.domain.model.medicine.medicineImage.*
import example.domain.model.takingrecord.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.data.domain.*
import java.time.*

@MyBatisQueryServiceTest
internal class MyBatisTakingRecordQueryServiceTest(@Autowired private val takingRecordQueryService: TakingRecordQueryService,
                                                   @Autowired private val testTakingRecordInserter: TestTakingRecordInserter,
                                                   @Autowired private val testMedicineInserter: TestMedicineInserter,
                                                   @Autowired private val testAccountInserter: TestAccountInserter,
                                                   @Autowired private val sharedGroupInserter: TestSharedGroupInserter) {
    private lateinit var requesterProfile: Profile
    private lateinit var userSession: UserSession
    private lateinit var requesterMedicine: Medicine

    @BeforeEach
    internal fun setUp() {
        requesterProfile = testAccountInserter.insertAccountAndProfile().second
        userSession = UserSessionFactory.create(requesterProfile.accountId)
        requesterMedicine = testMedicineInserter.insert(MedicineOwner.create(requesterProfile.accountId))
    }

    @Test
    @DisplayName("服用記録概要一覧を取得する")
    fun getTakingRecordOverviews() {
        //given:
        val (_, member) = testAccountInserter.insertAccountAndProfile()
        val sharedGroup = sharedGroupInserter.insert(members = setOf(requesterProfile.accountId, member.accountId))
        val sharedGroupMedicine = testMedicineInserter.insert(MedicineOwner.create(sharedGroup.id))

        val requesterTakingRecord = testTakingRecordInserter.insert(requesterProfile.accountId, sharedGroupMedicine.id)
        val memberTakingRecord = testTakingRecordInserter.insert(member.accountId, sharedGroupMedicine.id)
        val filter = TakingRecordOverviewsFilter(sharedGroupMedicine.id,
                                                 setOf(member.accountId),
                                                 null, null)

        //when:
        val actualPage1 = takingRecordQueryService.findTakingRecordOverviewsPage(userSession,
                                                                                 filter,
                                                                                 PageRequest.of(0, 3))
        val actualPage2 = takingRecordQueryService.findTakingRecordOverviewsPage(userSession,
                                                                                 filter,
                                                                                 PageRequest.of(1, 3))

        //then:
        assertThat(actualPage1.totalPages).isEqualTo(1)
        assertThat(actualPage1.content.size).isEqualTo(1)
        assertThat(actualPage2.content.size).isEqualTo(0)
        val expectedTakingRecordOverview = createTakingRecordOverview(member, sharedGroupMedicine, memberTakingRecord)
        assertThat(actualPage1.content[0]).isEqualTo(expectedTakingRecordOverview)

    }

    fun createTakingRecordOverview(profile: Profile,
                                   medicine: Medicine,
                                   takingRecord: TakingRecord): TakingRecordOverview {
        return TakingRecordOverview(takingRecord.id,
                                    takingRecord.followUp.beforeTaking,
                                    takingRecord.followUp.afterTaking,
                                    takingRecord.takenAt,
                                    medicine.id,
                                    medicine.medicineName,
                                    User(profile.accountId,
                                         profile.username,
                                         profile.profileImageURL))
    }

    @Nested
    inner class GetTakingRecordDetailTest {
        @Test
        @DisplayName("服用記録詳細を取得する")
        fun getTakingRecordDetail() {
            //given:
            val takingRecord = testTakingRecordInserter.insert(userSession.accountId, requesterMedicine.id)

            //when:
            val actual = takingRecordQueryService.findTakingRecordDetail(takingRecord.id, userSession)

            //then:
            val expected = TakingRecordDetail(takingRecord.id,
                                              requesterMedicine.id,
                                              requesterMedicine.medicineName,
                                              takingRecord.dose,
                                              requesterMedicine.dosageAndAdministration.takingUnit,
                                              takingRecord.followUp,
                                              takingRecord.note,
                                              takingRecord.takenAt,
                                              User(userSession.accountId,
                                                   requesterProfile.username,
                                                   requesterProfile.profileImageURL))
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        @DisplayName("服用記録が見つからなかった場合、服薬記録詳細の取得に失敗する")
        fun takingRecordNotFound_gettingTakingRecordDetailFails() {
            //given:
            val badTakingRecordId = TakingRecordId("NonexistentId")

            //when:
            val target: () -> Unit = { takingRecordQueryService.findTakingRecordDetail(badTakingRecordId, userSession) }

            //then:
            val takingRecordNotFoundException = assertThrows<TakingRecordNotFoundException>(target)
            assertThat(takingRecordNotFoundException.takingRecordId).isEqualTo(badTakingRecordId)
        }
    }
}