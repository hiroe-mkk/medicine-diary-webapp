package example.domain.model.medicine

import example.domain.model.account.*
import example.domain.model.sharedgroup.*

/**
 * 薬所有者
 */
data class MedicineOwner(val accountId: AccountId?,
                         val sharedGroupId: SharedGroupId? = null)