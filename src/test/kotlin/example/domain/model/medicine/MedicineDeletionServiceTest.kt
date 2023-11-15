package example.domain.model.medicine

import example.application.service.medicine.*
import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.medicine.medicineImage.*
import example.domain.model.sharedgroup.*
import example.domain.model.medicationrecord.*
import example.domain.shared.type.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import io.mockk.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@DomainLayerTest
internal class MedicineDeletionServiceTest(@Autowired private val medicineRepository: MedicineRepository,
                                           @Autowired private val medicationRecordRepository: MedicationRecordRepository,
                                           @Autowired private val medicineDeletionService: MedicineDeletionService,
                                           @Autowired private val testAccountInserter: TestAccountInserter,
                                           @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                           @Autowired private val testMedicineInserter: TestMedicineInserter,
                                           @Autowired private val testMedicationRecordInserter: TestMedicationRecordInserter) {
    private lateinit var requesterAccountId: AccountId

    @BeforeEach
    internal fun setUp() {
        requesterAccountId = testAccountInserter.insertAccountAndProfile().first.id
    }

    @Test
    @DisplayName("薬を削除する")
    fun deleteMedicine() {
        //given:
        val medicineImageURL = MedicineImageURL("endpoint", "/medicineimage/oldMedicineImage")
        val medicine = testMedicineInserter.insert(MedicineOwner.create(requesterAccountId),
                                                   medicineImageURL = medicineImageURL)
        val medicationRecord = testMedicationRecordInserter.insert(requesterAccountId, medicine.id)

        //when:
        medicineDeletionService.delete(medicine.id, requesterAccountId)

        //then:
        val foundMedicine = medicineRepository.findById(medicine.id)
        assertThat(foundMedicine).isNull()
        val foundMedicationRecord = medicationRecordRepository.findById(medicationRecord.id)
        assertThat(foundMedicationRecord).isNull()
    }

    @Test
    @DisplayName("共有グループの薬をすべて削除する")
    fun deleteAllSharedGroupMedicines() {
        //given
        val sharedGroup = testSharedGroupInserter.insert(members = setOf(requesterAccountId))
        val medicationRecordIds = List(3) {
            val medicine = testMedicineInserter.insert(MedicineOwner.create(sharedGroup.id))
            testMedicationRecordInserter.insert(requesterAccountId, medicine.id).id
        }

        //when:
        medicineDeletionService.deleteAllSharedGroupMedicines(sharedGroup.id)

        //then:
        val foundMedicines = medicineRepository.findByOwner(sharedGroup.id)
        assertThat(foundMedicines).isEmpty()
        val foundMedicationRecords = medicationRecordIds.mapNotNull { medicationRecordRepository.findById(it) }
        assertThat(foundMedicationRecords).isEmpty()
    }

    @Test
    @DisplayName("所有する薬をすべて削除する")
    fun deleteAllOwnedMedicines() {
        //given
        val medicationRecordIds = List(3) {
            val medicine = testMedicineInserter.insert(MedicineOwner.create(requesterAccountId))
            testMedicationRecordInserter.insert(requesterAccountId, medicine.id).id
        }

        //when:
        medicineDeletionService.deleteAllOwnedMedicines(requesterAccountId)

        //then:
        val foundMedicines = medicineRepository.findByOwner(requesterAccountId)
        assertThat(foundMedicines).isEmpty()
        val foundMedicationRecords = medicationRecordIds.mapNotNull { medicationRecordRepository.findById(it) }
        assertThat(foundMedicationRecords).isEmpty()
    }
}