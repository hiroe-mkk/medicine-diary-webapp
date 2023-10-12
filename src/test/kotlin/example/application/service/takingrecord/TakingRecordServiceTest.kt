package example.application.service.takingrecord

import com.ninjasquad.springmockk.*
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
    private val takingRecordDetailDtoFactory: TakingRecordDetailDtoFactory =
            TakingRecordDetailDtoFactory(profileRepository, medicineRepository)

    private val takingRecordService: TakingRecordService =
            TakingRecordService(takingRecordRepository, medicineRepository, takingRecordDetailDtoFactory)

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
                                                 TakenMedicine(medicine.id,
                                                               medicine.name,
                                                               takingRecord.dose,
                                                               medicine.dosageAndAdministration.takingUnit),
                                                 takingRecord.followUp,
                                                 takingRecord.note,
                                                 takingRecord.takenAt,
                                                 Recorder(userSession.accountId,
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
        @Test
        @DisplayName("服用記録を追加する")
        fun addTakingRecord() {
            //given:
            val command =
                    TestTakingRecordEditCommandFactory.createCompletedAdditionCommand(takenMedicine = medicine.id.value)

            //when:
            val newTakingRecordId = takingRecordService.addTakingRecord(command, userSession)

            //then:
            val foundTakingRecord = takingRecordRepository.findById(newTakingRecordId)
            val expected = TakingRecord.reconstruct(
                    newTakingRecordId,
                    userSession.accountId,
                    medicine.id,
                    command.validatedDose,
                    command.validFollowUp,
                    command.validatedNote,
                    command.validatedTakenAt)
            assertThat(foundTakingRecord).usingRecursiveComparison().isEqualTo(expected)
        }

        @Test
        @DisplayName("薬が見つからなかった場合、服用記録の追加に失敗する")
        fun medicineNotFound_addingTakingRecordFails() {
            //given:
            val badMedicineId = MedicineId("NonexistentId")
            val command =
                    TestTakingRecordEditCommandFactory.createCompletedAdditionCommand(takenMedicine = badMedicineId.value)

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
            val command =
                    TestTakingRecordEditCommandFactory.createCompletedAdditionCommand(takenMedicine = medicine.id.value)

            //when:
            val target: () -> Unit = { takingRecordService.addTakingRecord(command, userSession) }

            //then:
            assertThrows<OperationNotPermittedException>(target)
        }
    }

    @Nested
    inner class ModifyTakingRecordTest {
        private lateinit var takingRecord: TakingRecord
        private lateinit var command: TakingRecordEditCommand

        @BeforeEach
        internal fun setUp() {
            takingRecord = testTakingRecordInserter.insert(userSession.accountId, medicine.id)
            val newTakenMedicine = testMedicineInserter.insert(userSession.accountId).id.value
            command = TestTakingRecordEditCommandFactory.createCompletedModificationCommand(newTakenMedicine)
        }

        @Test
        @DisplayName("服用記録を修正する")
        fun modifyTakingRecord() {
            //when:
            takingRecordService.modifyTakingRecord(takingRecord.id, command, userSession)

            //then:
            val foundTakingRecord = takingRecordRepository.findById(takingRecord.id)
            val expected = TakingRecord.reconstruct(
                    takingRecord.id,
                    userSession.accountId,
                    command.validatedTakenMedicine,
                    command.validatedDose,
                    command.validFollowUp,
                    command.validatedNote,
                    command.validatedTakenAt)
            assertThat(foundTakingRecord).usingRecursiveComparison().isEqualTo(expected)
        }

        @Test
        @DisplayName("服用記録が見つからなかった場合、服用記録の修正に失敗する")
        fun takingRecordNotFound_modifyingTakingRecordFails() {
            //given:
            val badTakingRecordId = TakingRecordId("NonexistentId")

            //when:
            val target: () -> Unit = { takingRecordService.modifyTakingRecord(badTakingRecordId, command, userSession) }

            //then:
            val takingRecordNotFoundException = assertThrows<TakingRecordNotFoundException>(target)
            assertThat(takingRecordNotFoundException.takingRecordId).isEqualTo(badTakingRecordId)
        }

        @Test
        @DisplayName("ユーザーが記録していない服用記録の場合、服用記録の修正に失敗する")
        fun takingRecordIsNotRecordedByUser_modifyingTakingRecordFails() {
            //given:
            val (anotherAccount, _) = testAccountInserter.insertAccountAndProfile()
            val takingRecord = testTakingRecordInserter.insert(anotherAccount.id, medicine.id)

            //when:
            val target: () -> Unit = { takingRecordService.modifyTakingRecord(takingRecord.id, command, userSession) }

            //then:
            val takingRecordNotFoundException = assertThrows<TakingRecordNotFoundException>(target)
            assertThat(takingRecordNotFoundException.takingRecordId).isEqualTo(takingRecord.id)
        }

        @Test
        @DisplayName("薬が見つからなかった場合、服用記録の修正に失敗する")
        fun medicineNotFound_modifyingTakingRecordFails() {
            //given:
            val badMedicineId = MedicineId("NonexistentId")
            val command =
                    TestTakingRecordEditCommandFactory.createCompletedModificationCommand(takenMedicine = badMedicineId.value)

            //when:
            val target: () -> Unit = { takingRecordService.modifyTakingRecord(takingRecord.id, command, userSession) }

            //then:
            val medicineNotFoundException = assertThrows<MedicineNotFoundException>(target)
            assertThat(medicineNotFoundException.medicineId).isEqualTo(badMedicineId)
        }

        @Test
        @DisplayName("薬の所有者が服用記録の記録者と異なる場合、服用記録の修正に失敗する")
        fun ownerIsDifferentFromRecorder_modifyingTakingRecordFails() {
            //given:
            val (anotherAccount, _) = testAccountInserter.insertAccountAndProfile()
            val medicine = testMedicineInserter.insert(anotherAccount.id)
            val command =
                    TestTakingRecordEditCommandFactory.createCompletedModificationCommand(takenMedicine = medicine.id.value)

            //when:
            val target: () -> Unit = { takingRecordService.modifyTakingRecord(takingRecord.id, command, userSession) }

            //then:
            assertThrows<OperationNotPermittedException>(target)
        }
    }

    @Nested
    inner class DeleteTakingRecordTest {
        private lateinit var takingRecord: TakingRecord

        @BeforeEach
        internal fun setUp() {
            takingRecord = testTakingRecordInserter.insert(userSession.accountId, medicine.id)
        }

        @Test
        @DisplayName("服用記録を削除する")
        fun deleteTakingRecord() {
            //when:
            takingRecordService.deleteTakingRecord(takingRecord.id, userSession)

            //then:
            val foundTakingRecord = takingRecordRepository.findById(takingRecord.id)
            assertThat(foundTakingRecord).isNull()
        }

        @Test
        @DisplayName("服用記録が見つからなかった場合、服用記録の削除に失敗する")
        fun takingRecordNotFound_deletingTakingRecordFails() {
            //given:
            val badTakingRecordId = TakingRecordId("NonexistentId")

            //when:
            val target: () -> Unit = { takingRecordService.deleteTakingRecord(badTakingRecordId, userSession) }

            //then:
            val takingRecordNotFoundException = assertThrows<TakingRecordNotFoundException>(target)
            assertThat(takingRecordNotFoundException.takingRecordId).isEqualTo(badTakingRecordId)
        }

        @Test
        @DisplayName("ユーザーが記録していない服用記録の場合、服用記録の修正に失敗する")
        fun takingRecordIsNotRecordedByUser_deletingTakingRecordFails() {
            //given:
            val (anotherAccount, _) = testAccountInserter.insertAccountAndProfile()
            val takingRecord = testTakingRecordInserter.insert(anotherAccount.id, medicine.id)

            //when:
            val target: () -> Unit = { takingRecordService.deleteTakingRecord(takingRecord.id, userSession) }

            //then:
            val takingRecordNotFoundException = assertThrows<TakingRecordNotFoundException>(target)
            assertThat(takingRecordNotFoundException.takingRecordId).isEqualTo(takingRecord.id)
        }
    }
}