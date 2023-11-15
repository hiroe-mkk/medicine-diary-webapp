package example.domain.model.sharedgroup

import example.domain.model.account.*
import example.domain.model.medicine.*
import org.springframework.stereotype.*

@Component
class SharedGroupUnshareService(private val sharedGroupRepository: SharedGroupRepository,
                                private val sharedGroupQueryService: SharedGroupQueryService,
                                private val medicineDeletionService: MedicineDeletionService) {
    fun unshare(sharedGroupId: SharedGroupId, accountId: AccountId) {
        val sharedGroup = sharedGroupQueryService.findParticipatingSharedGroup(sharedGroupId, accountId) ?: return

        sharedGroup.unshare(accountId)
        if (sharedGroup.shouldDelete()) {
            medicineDeletionService.deleteAllSharedGroupMedicines(sharedGroup.id)
            sharedGroupRepository.deleteById(sharedGroupId)
        } else {
            sharedGroupRepository.save(sharedGroup)
        }
    }
}