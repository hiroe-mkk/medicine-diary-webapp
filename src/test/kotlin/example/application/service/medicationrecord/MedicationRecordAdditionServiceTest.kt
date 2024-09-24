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
internal class MedicationRecordAdditionServiceTest(@Autowired private val medicationRecordRepository: MedicationRecordRepository,
                                                   @Autowired private val medicineRepository: MedicineRepository,
                                                   @Autowired private val medicineFinder: MedicineFinder,
                                                   @Autowired private val testAccountInserter: TestAccountInserter,
                                                   @Autowired private val testMedicineInserter: TestMedicineInserter) {
    private val medicationRecordAdditionService = MedicationRecordAdditionService(medicationRecordRepository,
                                                                                  medicineRepository,
                                                                                  medicineFinder)

    private lateinit var userSession: UserSession

    @BeforeEach
    internal fun setUp() {
        userSession = UserSessionFactory.create(testAccountInserter.insertAccountAndProfile().first.id)
    }

    @Test
    @DisplayName("服用記録を追加する")
    fun addMedicationRecord() {
        //given:
        val inventory = Inventory(5.0, 12.0, null, null, 2)
        val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId),
                                                   inventory = inventory)
        val command = TestMedicationRecordFactory.createCompletedAdditionCommand(takenMedicine = medicine.id.value,
                                                                                 quantity = 1.0)

        //when:
        val newMedicationRecordId = medicationRecordAdditionService.addMedicationRecord(command, userSession)

        //then:
        val foundMedicationRecord = medicationRecordRepository.findById(newMedicationRecordId)
        val expected = MedicationRecord(newMedicationRecordId,
                                        userSession.accountId,
                                        medicine.id,
                                        command.validatedDose,
                                        command.validFollowUp,
                                        command.validatedNote,
                                        command.validatedTakenMedicineOn,
                                        command.validatedTakenMedicineAt,
                                        command.validatedSymptomOnsetAt,
                                        command.validatedOnsetEffectAt)
        assertThat(foundMedicationRecord).usingRecursiveComparison().isEqualTo(expected)
        val foundMedicine = medicineRepository.findById(medicine.id)
        val expectedInventory = Inventory(4.0, 12.0, null, null, 2)
        assertThat(foundMedicine?.inventory).isEqualTo(expectedInventory)
    }

    @Test
    @DisplayName("薬が見つからなかった場合、服用記録の追加に失敗する")
    fun medicineNotFound_addingMedicationRecordFails() {
        //given:
        val nonexistentMedicineId = MedicineId(EntityIdHelper.generate())
        val command =
                TestMedicationRecordFactory.createCompletedAdditionCommand(takenMedicine = nonexistentMedicineId.value)

        //when:
        val target: () -> Unit = { medicationRecordAdditionService.addMedicationRecord(command, userSession) }

        //then:
        val medicineNotFoundException = assertThrows<MedicineNotFoundException>(target)
        assertThat(medicineNotFoundException.medicineId).isEqualTo(nonexistentMedicineId)
    }
}
