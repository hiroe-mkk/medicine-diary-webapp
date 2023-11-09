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
                                                   @Autowired private val testAccountInserter: TestAccountInserter) {
    @Test
    @DisplayName("服用した薬をもとに服用記録概要一覧を取得する")
    fun getTakingRecordDetailsByTakenMedicine() {
        //given:
        val (account, profile) = testAccountInserter.insertAccountAndProfile(
                profileImageURL = ProfileImageURL("endpoint", "/path"))
        val userSession = UserSessionFactory.create(account.id)
        val medicine = testMedicineInserter.insert(MedicineOwner.create(account.id))

        val localDateTime = LocalDateTime.of(2020, 1, 1, 0, 0)
        val takingRecord = List(5) { index ->
            testTakingRecordInserter.insert(account.id,
                                            medicine.id,
                                            takenAt = localDateTime.plusDays(index.toLong()))
        }

        //when:
        val actualPage1 = takingRecordQueryService.findTakingRecordDetailsByTakenMedicine(medicine.id,
                                                                                          userSession,
                                                                                          PageRequest.of(0, 3))
        val actualPage2 = takingRecordQueryService.findTakingRecordDetailsByTakenMedicine(medicine.id,
                                                                                          userSession,
                                                                                          PageRequest.of(1, 3))

        //then:
        assertThat(actualPage1.totalPages).isEqualTo(2)
        assertThat(actualPage1.content.size).isEqualTo(3)
        assertThat(actualPage2.content.size).isEqualTo(2)
        val expectedPage1 = arrayOf(createTakingRecordOverview(profile, medicine, takingRecord[4]),
                                    createTakingRecordOverview(profile, medicine, takingRecord[3]),
                                    createTakingRecordOverview(profile, medicine, takingRecord[2]))
        assertThat(actualPage1.content).containsExactly(*expectedPage1)
        val expectedPage2 = arrayOf(createTakingRecordOverview(profile, medicine, takingRecord[1]),
                                    createTakingRecordOverview(profile, medicine, takingRecord[0]))
        assertThat(actualPage2.content).containsExactly(*expectedPage2)
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
        private lateinit var userSession: UserSession
        private lateinit var usersProfile: Profile
        private lateinit var medicine: Medicine

        @BeforeEach
        internal fun setUp() {
            val (account, profile) = testAccountInserter.insertAccountAndProfile()
            userSession = UserSessionFactory.create(account.id)
            usersProfile = profile
            medicine = testMedicineInserter.insert(MedicineOwner.create(account.id))
        }

        @Test
        @DisplayName("服用記録詳細を取得する")
        fun getTakingRecordDetail() {
            //given:
            val takingRecord = testTakingRecordInserter.insert(userSession.accountId, medicine.id)

            //when:
            val actual = takingRecordQueryService.findTakingRecordDetail(takingRecord.id, userSession)

            //then:
            val expected = TakingRecordDetail(takingRecord.id,
                                              medicine.id,
                                              medicine.medicineName,
                                              takingRecord.dose,
                                              medicine.dosageAndAdministration.takingUnit,
                                              takingRecord.followUp,
                                              takingRecord.note,
                                              takingRecord.takenAt,
                                              User(userSession.accountId,
                                                   usersProfile.username,
                                                   usersProfile.profileImageURL))
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

        @Test
        @DisplayName("ユーザーが記録していない服薬記録の場合、服薬記録詳細の取得に失敗する")
        fun takingRecordIsNotRecordedByUser_gettingTakingRecordDetailFails() {
            //given:
            val (anotherAccount, _) = testAccountInserter.insertAccountAndProfile()
            val takingRecord = testTakingRecordInserter.insert(anotherAccount.id, medicine.id)

            //when:
            val target: () -> Unit = { takingRecordQueryService.findTakingRecordDetail(takingRecord.id, userSession) }

            //then:
            val takingRecordNotFoundException = assertThrows<TakingRecordNotFoundException>(target)
            assertThat(takingRecordNotFoundException.takingRecordId).isEqualTo(takingRecord.id)
        }
    }
}