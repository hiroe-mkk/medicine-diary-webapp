package example.application.service.takingrecord

import example.application.service.medicine.*
import example.application.service.takingrecord.TakingRecordDetailDto.*
import example.application.shared.usersession.*
import example.domain.model.account.profile.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@MyBatisRepositoryTest
internal class TakingRecordServiceTest(@Autowired private val takingRecordRepository: TakingRecordRepository,
                                       @Autowired private val profileRepository: ProfileRepository,
                                       @Autowired private val medicineRepository: MedicineRepository,
                                       @Autowired private val testAccountInserter: TestAccountInserter,
                                       @Autowired private val testMedicineInserter: TestMedicineInserter,
                                       @Autowired private val testTakingRecordInserter: TestTakingRecordInserter) {
    private val takingRecordDetailDtoFactory: TakingRecordDetailDtoFactory =
            TakingRecordDetailDtoFactory(profileRepository, medicineRepository)
    private val takingRecordService: TakingRecordService =
            TakingRecordService(takingRecordRepository, takingRecordDetailDtoFactory)

    private lateinit var userSession: UserSession
    private lateinit var usersProfile: Profile
    private lateinit var medicine: Medicine

    @BeforeEach
    internal fun setUp() {
        val (account, profile) = testAccountInserter.insertAccountAndProfile()
        userSession = UserSessionFactory.create(account.id)
        usersProfile = profile
        medicine = testMedicineInserter.insert(account.id)
    }

    @Nested
    inner class GetTakingRecordDetailTest {
        @Test
        @DisplayName("服用記録詳細を取得する")
        fun getTakingRecordDetail() {
            //given:
            val takingRecord = testTakingRecordInserter.insert(userSession.accountId, medicine.id)

            //when:
            val actual = takingRecordService.findTakingRecordDetail(takingRecord.id, userSession)

            //then:
            val expected = TakingRecordDetailDto(takingRecord.id,
                                                 Recorder(userSession.accountId,
                                                          usersProfile.username,
                                                          usersProfile.profileImageURL),
                                                 TakenMedicine(medicine.id,
                                                               medicine.name),
                                                 takingRecord.dose,
                                                 takingRecord.symptoms,
                                                 takingRecord.note,
                                                 takingRecord.takenAt)
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        @DisplayName("服用記録が見つからなかった場合、服薬記録詳細の取得に失敗する")
        fun takingRecordNotFound_gettingTakingRecordDetailFails() {
            //given:
            val badTakingRecordId = TakingRecordId("NonexistentId")

            //when:
            val target: () -> Unit = { takingRecordService.findTakingRecordDetail(badTakingRecordId, userSession) }

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
            val target: () -> Unit = { takingRecordService.findTakingRecordDetail(takingRecord.id, userSession) }

            //then:
            val takingRecordNotFoundException = assertThrows<TakingRecordNotFoundException>(target)
            assertThat(takingRecordNotFoundException.takingRecordId).isEqualTo(takingRecord.id)
        }
    }
}