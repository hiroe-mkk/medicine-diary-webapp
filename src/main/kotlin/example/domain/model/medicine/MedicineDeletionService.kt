package example.domain.model.medicine

import example.domain.model.account.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.medicineimage.*
import example.domain.model.sharedgroup.*
import org.springframework.stereotype.*

@Component
class MedicineDeletionService(private val medicineRepository: MedicineRepository,
                              private val medicineImageStorage: MedicineImageStorage,
                              private val medicationRecordRepository: MedicationRecordRepository,
                              private val medicineQueryService: MedicineQueryService) {
    fun deleteOwnedMedicineAndMedicationRecords(medicineId: MedicineId, accountId: AccountId) {
        val medicine = medicineQueryService.findAvailableMedicine(medicineId, accountId) ?: return
        medicationRecordRepository.deleteAllByTakenMedicine(medicineId)
        medicineRepository.deleteById(medicine.id)
        medicine.medicineImageURL?.let { medicineImageStorage.delete(it) }
    }

    fun deleteAllOwnedMedicinesAndMedicationRecords(accountId: AccountId) {
        val ownedMedicines = medicineQueryService.findAllOwnedMedicines(accountId)
        if (ownedMedicines.isEmpty()) return

        ownedMedicines.forEach { medicationRecordRepository.deleteAllByTakenMedicine(it.id) }
        medicineRepository.deleteByOwner(accountId)
        medicineImageStorage.deleteAll(ownedMedicines.mapNotNull { it.medicineImageURL })
    }

    fun deleteAllSharedGroupMedicinesAndMedicationRecords(sharedGroupId: SharedGroupId) {
        val sharedGroupMedicines = medicineRepository.findByOwner(sharedGroupId)
        if (sharedGroupMedicines.isEmpty()) return

        sharedGroupMedicines.forEach { medicationRecordRepository.deleteAllByTakenMedicine(it.id) }
        medicineRepository.deleteByOwner(sharedGroupId)
        medicineImageStorage.deleteAll(sharedGroupMedicines.mapNotNull { it.medicineImageURL })
    }
}