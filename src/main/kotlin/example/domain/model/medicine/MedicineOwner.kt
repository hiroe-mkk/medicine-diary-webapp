package example.domain.model.medicine

import example.domain.model.account.*
import example.domain.model.sharedgroup.*

/**
 * 薬所有者
 */
data class MedicineOwner(val accountId: AccountId?,
                         val sharedGroupId: SharedGroupId?) {
    companion object {
        fun create(accountId: AccountId): MedicineOwner {
            return MedicineOwner(accountId, null)
        }

        fun create(sharedGroupId: SharedGroupId): MedicineOwner {
            return MedicineOwner(null, sharedGroupId)
        }
    }

    val isAccount = accountId != null
    val isSharedGroup = sharedGroupId != null
}