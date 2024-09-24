package example.application.service.medicationrecord

import example.application.service.medicine.*
import example.application.shared.usersession.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import example.infrastructure.db.repository.shared.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@DomainLayerTest
class MedicationRecordModificationServiceTest(@Autowired private val medicationRecordRepository: MedicationRecordRepository,
                                              @Autowired private val medicineFinder: MedicineFinder,
                                              @Autowired private val medicationRecordFinder: MedicationRecordFinder,
                                              @Autowired private val testAccountInserter: TestAccountInserter,
                                              @Autowired private val testMedicineInserter: TestMedicineInserter,
                                              @Autowired private val testMedicationRecordInserter: TestMedicationRecordInserter) {
    private val medicationRecordModificationService = MedicationRecordModificationService(medicationRecordRepository,
                                                                                          medicationRecordFinder,
                                                                                          medicineFinder)

    private lateinit var userSession: UserSession
    private lateinit var medicationRecord: MedicationRecord
    private lateinit var command: MedicationRecordEditCommand

    @BeforeEach
    internal fun setUp() {
        userSession = UserSessionFactory.create(testAccountInserter.insertAccountAndProfile().first.id)
        val medicine1 = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))
        val medicine2 = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))
        medicationRecord = testMedicationRecordInserter.insert(userSession.accountId, medicine1.id)
        command = TestMedicationRecordFactory.createCompletedModificationCommand(medicine2.id.value)
    }

    @Test
    @DisplayName("服用記録を修正する")
    fun modifyMedicationRecord() {
        //when:
        medicationRecordModificationService.modifyMedicationRecord(medicationRecord.id, command, userSession)

        //then:
        val foundMedicationRecord = medicationRecordRepository.findById(medicationRecord.id)
        val expected = MedicationRecord(medicationRecord.id,
                                        userSession.accountId,
                                        command.validatedTakenMedicine,
                                        command.validatedDose,
                                        command.validFollowUp,
                                        command.validatedNote,
                                        command.validatedTakenMedicineOn,
                                        command.validatedTakenMedicineAt,
                                        command.validatedSymptomOnsetAt,
                                        command.validatedOnsetEffectAt)
        assertThat(foundMedicationRecord).usingRecursiveComparison().isEqualTo(expected)
    }

    @Test
    @DisplayName("服用記録が見つからなかった場合、服用記録の修正に失敗する")
    fun medicationRecordNotFound_modifyingMedicationRecordFails() {
        //given:
        val nonexistentMedicationRecordId = MedicationRecordId(EntityIdHelper.generate())

        //when:
        val target: () -> Unit = {
            medicationRecordModificationService.modifyMedicationRecord(nonexistentMedicationRecordId,
                                                                       command,
                                                                       userSession)
        }

        //then:
        val medicationRecordNotFoundException = assertThrows<MedicationRecordNotFoundException>(target)
        assertThat(medicationRecordNotFoundException.medicationRecordId).isEqualTo(nonexistentMedicationRecordId)
    }

    @Test
    @DisplayName("薬が見つからなかった場合、服用記録の修正に失敗する")
    fun medicineNotFound_modifyingMedicationRecordFails() {
        //given:
        val nonexistentMedicineId = MedicineId(EntityIdHelper.generate())
        val command = TestMedicationRecordFactory.createCompletedModificationCommand(nonexistentMedicineId.value)

        //when:
        val target: () -> Unit = {
            medicationRecordModificationService.modifyMedicationRecord(medicationRecord.id,
                                                                       command,
                                                                       userSession)
        }

        //then:
        val medicineNotFoundException = assertThrows<MedicineNotFoundException>(target)
        assertThat(medicineNotFoundException.medicineId).isEqualTo(nonexistentMedicineId)
    }
}
