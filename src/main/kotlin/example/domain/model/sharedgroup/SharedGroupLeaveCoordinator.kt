package example.domain.model.sharedgroup

import example.domain.model.account.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import example.domain.model.medicine.medicineimage.*
import org.springframework.stereotype.*

@Component
class SharedGroupLeaveCoordinator(private val sharedGroupRepository: SharedGroupRepository,
                                  private val medicineRepository: MedicineRepository,
                                  private val medicineImageStorage: MedicineImageStorage,
                                  private val medicationRecordRepository: MedicationRecordRepository,
                                  private val medicineFinder: MedicineFinder,
                                  private val medicineOwnerFactory: MedicineOwnerFactory,
                                  private val medicineDeletionCoordinator: MedicineDeletionCoordinator) {
    fun leaveSharedGroupAndCloneMedicines(accountId: AccountId) {
        val sharedGroupMedicines = medicineFinder.findAllSharedGroupMedicines(accountId)
        sharedGroupMedicines.forEach { sharedGroupMedicine ->
            val newMedicineOwner = medicineOwnerFactory.create(accountId, false)
            val newMedicineImageURL = sharedGroupMedicine.medicineImageURL?.let {
                val newMedicineImageURL = medicineImageStorage.createURL()
                medicineImageStorage.copy(it, newMedicineImageURL)
                newMedicineImageURL
            }
            val newMedicine = sharedGroupMedicine.clone(medicineRepository.createMedicineId(),
                                                        newMedicineOwner,
                                                        newMedicineImageURL)
            medicineRepository.save(newMedicine)

            val medicationRecords = medicationRecordRepository.findByTakenMedicineAndRecorder(sharedGroupMedicine.id,
                                                                                              accountId)
            medicationRecords.forEach { it.changeTakenMedicine(newMedicine.id) }
            medicationRecordRepository.saveAll(medicationRecords)
        }

        leaveSharedGroup(accountId)
    }

    fun leaveSharedGroup(accountId: AccountId) {
        val sharedGroup = sharedGroupRepository.findByMember(accountId) ?: return

        sharedGroup.leave(accountId)
        if (sharedGroup.shouldDelete()) {
            medicineDeletionCoordinator.deleteAllSharedGroupMedicinesAndMedicationRecords(sharedGroup.id)
            sharedGroupRepository.deleteById(sharedGroup.id)
        } else {
            sharedGroupRepository.save(sharedGroup)
        }
    }
}
