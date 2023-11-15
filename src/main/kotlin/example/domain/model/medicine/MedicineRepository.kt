package example.domain.model.medicine

import example.domain.model.account.*
import example.domain.model.sharedgroup.*

interface MedicineRepository {
    fun createMedicineId(): MedicineId

    fun findById(medicineId: MedicineId): Medicine?

    fun findByOwner(accountId: AccountId): Set<Medicine>

    fun findByOwners(accountIds: Collection<AccountId>): Set<Medicine>

    fun findByOwner(sharedGroupId: SharedGroupId): Set<Medicine>

    fun save(medicine: Medicine)

    fun deleteById(medicineId: MedicineId)

    fun deleteByOwner(sharedGroupId: SharedGroupId)
}