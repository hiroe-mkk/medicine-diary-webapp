package example.domain.model.medicine

import example.domain.model.account.*
import example.domain.model.medicine.medicineImage.*
import example.domain.model.sharedgroup.*
import example.domain.model.medicationrecord.*
import org.springframework.stereotype.*

@Component
class MedicineDeletionService(private val medicineRepository: MedicineRepository,
                              private val medicineImageStorage: MedicineImageStorage,
                              private val medicationRecordRepository: MedicationRecordRepository,
                              private val medicineQueryService: MedicineQueryService) {
    fun delete(medicineId: MedicineId, accountId: AccountId) {
        val medicine = medicineQueryService.findAvailableMedicine(medicineId, accountId) ?: return
        medicationRecordRepository.deleteAllByTakenMedicine(medicineId)
        medicineRepository.deleteById(medicine.id)
        medicine.medicineImageURL?.let { medicineImageStorage.delete(it) }
    }

    fun deleteAllSharedGroupMedicines(sharedGroupId: SharedGroupId) {
        val medicines = medicineRepository.findByOwner(sharedGroupId)
        if (medicines.isEmpty()) return

        medicines.forEach { medicationRecordRepository.deleteAllByTakenMedicine(it.id) }
        medicineRepository.deleteByOwner(sharedGroupId)
        medicineImageStorage.deleteAll(medicines.mapNotNull { it.medicineImageURL })
    }

    fun deleteAllOwnedMedicines(accountId: AccountId) {
        val medicines = medicineQueryService.findAllOwnedMedicines(accountId)
        if (medicines.isEmpty()) return

        medicines.forEach { medicationRecordRepository.deleteAllByTakenMedicine(it.id) }
        medicineRepository.deleteByOwner(accountId)
        medicineImageStorage.deleteAll(medicines.mapNotNull { it.medicineImageURL })
    }
}