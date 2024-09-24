package example.domain.model.medicine

import example.domain.model.account.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.medicineimage.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import io.mockk.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@DomainLayerTest
class MedicineDeletionCoordinatorTest(@Autowired private val medicineRepository: MedicineRepository,
                                      @Autowired private val medicationRecordRepository: MedicationRecordRepository,
                                      @Autowired private val medicineImageStorage: MedicineImageStorage,
                                      @Autowired private val medicineDeletionCoordinator: MedicineDeletionCoordinator,
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
    @DisplayName("所有する薬とその服用記録を削除する")
    fun deleteOwnedMedicineAndMedicationRecords() {
        //given:
        val medicineImageURL = medicineImageStorage.createURL()
        val medicine = testMedicineInserter.insert(MedicineOwner.create(requesterAccountId),
                                                   medicineImageURL = medicineImageURL)
        val medicationRecord = testMedicationRecordInserter.insert(requesterAccountId, medicine.id)

        //when:
        medicineDeletionCoordinator.deleteOwnedMedicineAndMedicationRecords(medicine.id, requesterAccountId)

        //then:
        val foundMedicine = medicineRepository.findById(medicine.id)
        assertThat(foundMedicine).isNull()
        val foundMedicationRecord = medicationRecordRepository.findById(medicationRecord.id)
        assertThat(foundMedicationRecord).isNull()
        verify(exactly = 1) { medicineImageStorage.delete(medicineImageURL) }
    }

    @Test
    @DisplayName("全ての所有する薬とその服用記録を削除する")
    fun deleteAllOwnedMedicinesAndMedicationRecords() {
        //given
        val ownedMedicines = List(3) {
            val medicine = testMedicineInserter.insert(MedicineOwner.create(requesterAccountId))
            testMedicationRecordInserter.insert(requesterAccountId, medicine.id)
            medicine
        }

        //when:
        medicineDeletionCoordinator.deleteAllOwnedMedicinesAndMedicationRecords(requesterAccountId)

        //then:
        val foundMedicines = medicineRepository.findByOwner(requesterAccountId)
        assertThat(foundMedicines).isEmpty()
        val foundMedicationRecords = ownedMedicines.flatMap { medicationRecordRepository.findByTakenMedicine(it.id) }
        assertThat(foundMedicationRecords).isEmpty()
    }

    @Test
    @DisplayName("全ての共有グループの薬とその服用記録を削除する")
    fun deleteAllSharedGroupMedicinesAndMedicationRecords() {
        //given
        val sharedGroup = testSharedGroupInserter.insert(members = setOf(requesterAccountId))
        val sharedGroupMedicines = List(3) {
            val medicine = testMedicineInserter.insert(MedicineOwner.create(sharedGroup.id))
            testMedicationRecordInserter.insert(requesterAccountId, medicine.id)
            medicine
        }

        //when:
        medicineDeletionCoordinator.deleteAllSharedGroupMedicinesAndMedicationRecords(sharedGroup.id)

        //then:
        val foundMedicines = medicineRepository.findByOwner(sharedGroup.id)
        assertThat(foundMedicines).isEmpty()
        val foundMedicationRecords =
                sharedGroupMedicines.flatMap { medicationRecordRepository.findByTakenMedicine(it.id) }
        assertThat(foundMedicationRecords).isEmpty()
    }
}
