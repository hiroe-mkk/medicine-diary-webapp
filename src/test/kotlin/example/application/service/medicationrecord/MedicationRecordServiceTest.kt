package example.application.service.medicationrecord

import com.ninjasquad.springmockk.*
import example.application.query.medicationrecord.*
import example.application.service.medicine.*
import example.application.shared.usersession.*
import example.domain.model.account.profile.*
import example.domain.model.medicine.*
import example.domain.model.sharedgroup.*
import example.domain.model.medicationrecord.*
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

@DomainLayerTest
internal class MedicationRecordServiceTest(@Autowired private val medicationRecordRepository: MedicationRecordRepository,
                                           @Autowired private val medicineQueryService: MedicineQueryService,
                                           @Autowired private val medicationRecordQueryService: MedicationRecordQueryService,
                                           @Autowired private val testAccountInserter: TestAccountInserter,
                                           @Autowired private val testMedicineInserter: TestMedicineInserter,
                                           @Autowired private val testMedicationRecordInserter: TestMedicationRecordInserter) {
    private val medicationRecordService: MedicationRecordService =
            MedicationRecordService(medicationRecordRepository, medicationRecordQueryService, medicineQueryService)

    private lateinit var userSession: UserSession
    private lateinit var requesterMedicine: Medicine

    @BeforeEach
    internal fun setUp() {
        val requesterAccountId = testAccountInserter.insertAccountAndProfile().first.id
        userSession = UserSessionFactory.create(requesterAccountId)
        requesterMedicine = testMedicineInserter.insert(MedicineOwner.create(requesterAccountId))
    }

    @Nested
    inner class AddMedicationRecordTest {
        @Test
        @DisplayName("服用記録を追加する")
        fun addMedicationRecord() {
            //given:
            val command =
                    TestMedicationRecordFactory.createCompletedAdditionCommand(takenMedicine = requesterMedicine.id.value)

            //when:
            val newMedicationRecordId = medicationRecordService.addMedicationRecord(command, userSession)

            //then:
            val foundMedicationRecord = medicationRecordRepository.findById(newMedicationRecordId)
            val expected = MedicationRecord.reconstruct(newMedicationRecordId,
                                                        userSession.accountId,
                                                        requesterMedicine.id,
                                                        command.validatedDose,
                                                        command.validFollowUp,
                                                        command.validatedNote,
                                                        command.validatedTakenAt)
            assertThat(foundMedicationRecord).usingRecursiveComparison().isEqualTo(expected)
        }

        @Test
        @DisplayName("薬が見つからなかった場合、服用記録の追加に失敗する")
        fun medicineNotFound_addingMedicationRecordFails() {
            //given:
            val badMedicineId = MedicineId("NonexistentId")
            val command =
                    TestMedicationRecordFactory.createCompletedAdditionCommand(takenMedicine = badMedicineId.value)

            //when:
            val target: () -> Unit = { medicationRecordService.addMedicationRecord(command, userSession) }

            //then:
            val medicineNotFoundException = assertThrows<MedicineNotFoundException>(target)
            assertThat(medicineNotFoundException.medicineId).isEqualTo(badMedicineId)
        }
    }

    @Nested
    inner class ModifyMedicationRecordTest {
        private lateinit var medicationRecord: MedicationRecord
        private lateinit var command: MedicationRecordEditCommand

        @BeforeEach
        internal fun setUp() {
            medicationRecord = testMedicationRecordInserter.insert(userSession.accountId, requesterMedicine.id)
            val newTakenMedicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId)).id.value
            command = TestMedicationRecordFactory.createCompletedModificationCommand(newTakenMedicine)
        }

        @Test
        @DisplayName("服用記録を修正する")
        fun modifyMedicationRecord() {
            //when:
            medicationRecordService.modifyMedicationRecord(medicationRecord.id, command, userSession)

            //then:
            val foundMedicationRecord = medicationRecordRepository.findById(medicationRecord.id)
            val expected = MedicationRecord.reconstruct(
                    medicationRecord.id,
                    userSession.accountId,
                    command.validatedTakenMedicine,
                    command.validatedDose,
                    command.validFollowUp,
                    command.validatedNote,
                    command.validatedTakenAt)
            assertThat(foundMedicationRecord).usingRecursiveComparison().isEqualTo(expected)
        }

        @Test
        @DisplayName("服用記録が見つからなかった場合、服用記録の修正に失敗する")
        fun medicationRecordNotFound_modifyingMedicationRecordFails() {
            //given:
            val badMedicationRecordId = MedicationRecordId("NonexistentId")

            //when:
            val target: () -> Unit =
                    { medicationRecordService.modifyMedicationRecord(badMedicationRecordId, command, userSession) }

            //then:
            val medicationRecordNotFoundException = assertThrows<MedicationRecordNotFoundException>(target)
            assertThat(medicationRecordNotFoundException.medicationRecordId).isEqualTo(badMedicationRecordId)
        }

        @Test
        @DisplayName("薬が見つからなかった場合、服用記録の修正に失敗する")
        fun medicineNotFound_modifyingMedicationRecordFails() {
            //given:
            val badMedicineId = MedicineId("NonexistentId")
            val command =
                    TestMedicationRecordFactory.createCompletedModificationCommand(takenMedicine = badMedicineId.value)

            //when:
            val target: () -> Unit =
                    { medicationRecordService.modifyMedicationRecord(medicationRecord.id, command, userSession) }

            //then:
            val medicineNotFoundException = assertThrows<MedicineNotFoundException>(target)
            assertThat(medicineNotFoundException.medicineId).isEqualTo(badMedicineId)
        }
    }

    @Nested
    inner class DeleteMedicationRecordTest {
        private lateinit var medicationRecord: MedicationRecord

        @BeforeEach
        internal fun setUp() {
            medicationRecord = testMedicationRecordInserter.insert(userSession.accountId, requesterMedicine.id)
        }

        @Test
        @DisplayName("服用記録を削除する")
        fun deleteMedicationRecord() {
            //when:
            medicationRecordService.deleteMedicationRecord(medicationRecord.id, userSession)

            //then:
            val foundMedicationRecord = medicationRecordRepository.findById(medicationRecord.id)
            assertThat(foundMedicationRecord).isNull()
        }
    }
}