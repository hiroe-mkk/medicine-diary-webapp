package example.application.service.takingrecord

import example.application.service.medicine.*
import example.application.service.takingrecord.TakingRecordDetailDto.*
import example.application.shared.usersession.*
import example.domain.model.account.profile.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import example.domain.shared.exception.*
import example.domain.shared.type.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import io.mockk.*
import io.mockk.impl.annotations.*
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import java.time.*

@MyBatisRepositoryTest
internal class TakingRecordServiceTest(@Autowired private val takingRecordRepository: TakingRecordRepository,
                                       @Autowired private val profileRepository: ProfileRepository,
                                       @Autowired private val medicineRepository: MedicineRepository,
                                       @Autowired private val testAccountInserter: TestAccountInserter,
                                       @Autowired private val testMedicineInserter: TestMedicineInserter,
                                       @Autowired private val testTakingRecordInserter: TestTakingRecordInserter) {
    @MockK
    private lateinit var localDateTimeProvider: LocalDateTimeProvider

    private val takingRecordDetailDtoFactory: TakingRecordDetailDtoFactory =
            TakingRecordDetailDtoFactory(profileRepository, medicineRepository)

    @InjectMockKs
    private lateinit var takingRecordService: TakingRecordService

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

    @Nested
    inner class AddTakingRecordTest {
        private val localDateTime = LocalDateTime.of(2020, 1, 1, 0, 0)

        @BeforeEach
        internal fun setUp() {
            every { localDateTimeProvider.now() } returns localDateTime
        }

        @Test
        @DisplayName("服用記録を追加する")
        fun addTakingRecord() {
            //given:
            val command = TestTakingRecordFactory.createTakingRecordEditCommand(medicineId = medicine.id.value)

            //when:
            val newTakingRecordId = takingRecordService.addTakingRecord(command, userSession)

            //then:
            val foundTakingRecord = takingRecordRepository.findById(newTakingRecordId)
            val expected = TakingRecord.reconstruct(newTakingRecordId,
                                                    userSession.accountId,
                                                    medicine.id,
                                                    command.validatedDose,
                                                    command.validSymptoms,
                                                    command.validatedNote,
                                                    localDateTime)
            assertThat(foundTakingRecord).usingRecursiveComparison().isEqualTo(expected)
        }

        @Test
        @DisplayName("薬が見つからなかった場合、服用記録の追加に失敗する")
        fun medicineNotFound_addingTakingRecordFails() {
            //given:
            val badMedicineId = MedicineId("NonexistentId")
            val command = TestTakingRecordFactory.createTakingRecordEditCommand(medicineId = badMedicineId.value)

            //when:
            val target: () -> Unit = { takingRecordService.addTakingRecord(command, userSession) }

            //then:
            val medicineNotFoundException = assertThrows<MedicineNotFoundException>(target)
            assertThat(medicineNotFoundException.medicineId).isEqualTo(badMedicineId)
        }

        @Test
        @DisplayName("薬の所有者が服用記録の記録者と異なる場合、服用記録の追加に失敗する")
        fun ownerIsDifferentFromRecorder_addingTakingRecordFails() {
            //given:
            val (anotherAccount, _) = testAccountInserter.insertAccountAndProfile()
            val medicine = testMedicineInserter.insert(anotherAccount.id)
            val command = TestTakingRecordFactory.createTakingRecordEditCommand(medicineId = medicine.id.value)

            //when:
            val target: () -> Unit = { takingRecordService.addTakingRecord(command, userSession) }

            //then:
            assertThrows<OperationNotPermittedException>(target)
        }
    }
}