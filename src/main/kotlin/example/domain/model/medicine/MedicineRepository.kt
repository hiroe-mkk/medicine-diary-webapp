package example.domain.model.medicine

import example.domain.model.account.*
import example.domain.model.sharedgroup.*

interface MedicineRepository {
    fun createMedicineId(): MedicineId

    fun findById(medicineId: MedicineId): Medicine?

    fun findByAccountId(accountId: AccountId): List<Medicine>

    fun findBySharedGroupId(sharedGroupId: SharedGroupId): List<Medicine>

    fun save(medicine: Medicine)

    fun delete(medicineId: MedicineId)
}