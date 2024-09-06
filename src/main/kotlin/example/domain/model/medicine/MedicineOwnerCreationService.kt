package example.domain.model.medicine

import example.domain.model.account.*
import example.domain.model.sharedgroup.*
import org.springframework.stereotype.*

@Component
class MedicineOwnerCreationService(private val sharedGroupRepository: SharedGroupRepository) {
    fun createSharedGroupOwner(accountId: AccountId): MedicineOwner {
        val joinedSharedGroup = sharedGroupRepository.findByMember(accountId)
        return if (joinedSharedGroup != null) {
            MedicineOwner.create(joinedSharedGroup.id)
        } else {
            MedicineOwner.create(accountId)
        }
    }

    fun createAccountOwner(accountId: AccountId): MedicineOwner {
        return MedicineOwner.create(accountId)
    }
}
