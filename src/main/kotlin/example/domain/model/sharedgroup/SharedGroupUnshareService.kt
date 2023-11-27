package example.domain.model.sharedgroup

import example.domain.model.account.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import org.springframework.stereotype.*

@Component
class SharedGroupUnshareService(private val sharedGroupRepository: SharedGroupRepository,
                                private val medicineRepository: MedicineRepository,
                                private val medicationRecordRepository: MedicationRecordRepository,
                                private val sharedGroupQueryService: SharedGroupQueryService,
                                private val medicineQueryService: MedicineQueryService,
                                private val medicineOwnerCreationService: MedicineOwnerCreationService,
                                private val medicineDeletionService: MedicineDeletionService) {
    fun cloneSharedGroupMedicinesAndUnshare(accountId: AccountId) {
        val sharedGroupMedicines = medicineQueryService.findAllSharedGroupMedicines(accountId)
        sharedGroupMedicines.forEach { sharedGroupMedicine ->
            val newMedicineOwner = medicineOwnerCreationService.createAccountOwner(accountId)
            val newMedicine = sharedGroupMedicine.cloneWithMedicineIdAndOwner(medicineRepository.createMedicineId(),
                                                                              newMedicineOwner)
            medicineRepository.save(newMedicine)

            val medicationRecords = medicationRecordRepository.findByTakenMedicineAndRecorder(sharedGroupMedicine.id,
                                                                                              accountId)
            medicationRecords.forEach { it.changeTakenMedicine(newMedicine.id) }
            medicationRecordRepository.saveAll(medicationRecords)
        }

        unshare(accountId)
    }

    fun rejectAllInvitationAndUnshare(accountId: AccountId) {
        val invitedSharedGroups = sharedGroupQueryService.findInvitedSharedGroups(accountId)
        invitedSharedGroups.forEach {
            it.rejectInvitation(accountId)
            sharedGroupRepository.save(it)
        }

        unshare(accountId)
    }

    private fun unshare(accountId: AccountId) {
        val sharedGroup = sharedGroupQueryService.findParticipatingSharedGroup(accountId) ?: return
        sharedGroup.unshare(accountId)
        if (sharedGroup.shouldDelete()) {
            medicineDeletionService.deleteAllSharedGroupMedicinesAndMedicationRecords(sharedGroup.id)
            sharedGroupRepository.deleteById(sharedGroup.id)
        } else {
            sharedGroupRepository.save(sharedGroup)
        }
    }
}