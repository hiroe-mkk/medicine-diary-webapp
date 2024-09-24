package example.domain.model.medicine

import example.domain.model.account.*
import example.domain.model.sharedgroup.*
import org.springframework.stereotype.*

@Component
class MedicineOwnerFactory(private val sharedGroupRepository: SharedGroupRepository) {
    fun create(accountId: AccountId, isOwnedBySharedGroup: Boolean): MedicineOwner {
        val joinedSharedGroup = sharedGroupRepository.findByMember(accountId)

        return if (isOwnedBySharedGroup && joinedSharedGroup != null) {
            MedicineOwner.create(joinedSharedGroup.id)
        } else {
            MedicineOwner.create(accountId)
        }
    }
}
