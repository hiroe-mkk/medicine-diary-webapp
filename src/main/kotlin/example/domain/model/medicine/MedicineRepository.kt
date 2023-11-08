package example.domain.model.medicine

import example.domain.model.account.*
import example.domain.model.sharedgroup.*

interface MedicineRepository {
    fun createMedicineId(): MedicineId

    fun findById(medicineId: MedicineId): Medicine?

    fun findByAccountId(accountId: AccountId): Set<Medicine>

    fun findByAccountIds(accountIds: Collection<AccountId>): Set<Medicine>

    fun findBySharedGroupId(sharedGroupId: SharedGroupId): Set<Medicine>

    fun save(medicine: Medicine)

    fun delete(medicineId: MedicineId)
}