package example.domain.model.sharedgroup

import example.domain.model.account.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import example.domain.model.medicine.medicineimage.*
import org.springframework.stereotype.*

@Component
class SharedGroupLeaveService(private val sharedGroupRepository: SharedGroupRepository,
                              private val medicineRepository: MedicineRepository,
                              private val medicineImageStorage: MedicineImageStorage,
                              private val medicationRecordRepository: MedicationRecordRepository,
                              private val sharedGroupQueryService: SharedGroupQueryService,
                              private val medicineQueryService: MedicineQueryService,
                              private val medicineOwnerCreationService: MedicineOwnerCreationService,
                              private val medicineDeletionService: MedicineDeletionService) {
    fun leaveAndCloneMedicines(accountId: AccountId) {
        val sharedGroupMedicines = medicineQueryService.findAllSharedGroupMedicines(accountId)
        sharedGroupMedicines.forEach { sharedGroupMedicine ->
            val newMedicineOwner = medicineOwnerCreationService.createAccountOwner(accountId)
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

    fun leaveAndRejectAllInvitation(accountId: AccountId) {
        val invitedSharedGroups = sharedGroupQueryService.findInvitedSharedGroups(accountId)
        invitedSharedGroups.forEach {
            it.rejectInvitation(accountId)
            sharedGroupRepository.save(it)
        }

        leaveSharedGroup(accountId)
    }

    private fun leaveSharedGroup(accountId: AccountId) {
        val sharedGroup = sharedGroupQueryService.findParticipatingSharedGroup(accountId) ?: return
        sharedGroup.leave(accountId)
        if (sharedGroup.shouldDelete()) {
            medicineDeletionService.deleteAllSharedGroupMedicinesAndMedicationRecords(sharedGroup.id)
            sharedGroupRepository.deleteById(sharedGroup.id)
        } else {
            sharedGroupRepository.save(sharedGroup)
        }
    }
}
