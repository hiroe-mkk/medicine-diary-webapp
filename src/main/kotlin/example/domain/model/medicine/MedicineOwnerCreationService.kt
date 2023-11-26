package example.domain.model.medicine

import example.domain.model.account.*
import example.domain.model.sharedgroup.*
import org.springframework.stereotype.*

@Component
class MedicineOwnerCreationService(private val sharedGroupQueryService: SharedGroupQueryService) {
    fun createSharedGroupOwner(accountId: AccountId): MedicineOwner {
        val participatingSharedGroup = sharedGroupQueryService.findParticipatingSharedGroup(accountId)
        return if (participatingSharedGroup != null) {
            MedicineOwner.create(participatingSharedGroup.id)
        } else {
            MedicineOwner.create(accountId)
        }
    }

    fun createAccountOwner(accountId: AccountId): MedicineOwner {
        return MedicineOwner.create(accountId)
    }
}