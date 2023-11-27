package example.domain.model.sharedgroup

import example.domain.model.account.*
import example.domain.model.medicine.*
import org.springframework.stereotype.*

@Component
class SharedGroupUnshareService(private val sharedGroupRepository: SharedGroupRepository,
                                private val sharedGroupQueryService: SharedGroupQueryService,
                                private val medicineDeletionService: MedicineDeletionService) {
    fun unshare(accountId: AccountId) {
        val sharedGroup = sharedGroupQueryService.findParticipatingSharedGroup(accountId) ?: return

        sharedGroup.unshare(accountId)
        if (sharedGroup.shouldDelete()) {
            medicineDeletionService.deleteAllSharedGroupMedicinesAndMedicationRecords(sharedGroup.id)
            sharedGroupRepository.deleteById(sharedGroup.id)
        } else {
            sharedGroupRepository.save(sharedGroup)
        }
    }

    fun unshareAndRejectAllInvitation(accountId: AccountId) {
        unshare(accountId)

        val invitedSharedGroups = sharedGroupQueryService.findInvitedSharedGroups(accountId)
        invitedSharedGroups.forEach {
            it.rejectInvitation(accountId)
            sharedGroupRepository.save(it)
        }
    }
}