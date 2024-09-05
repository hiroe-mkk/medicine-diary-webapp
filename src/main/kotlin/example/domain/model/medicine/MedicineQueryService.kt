package example.domain.model.medicine

import example.domain.model.account.*
import example.domain.model.sharedgroup.*
import org.springframework.stereotype.*

@Component
class MedicineQueryService(private val medicineRepository: MedicineRepository,
                           private val sharedGroupRepository: SharedGroupRepository) {
    fun findAllOwnedMedicines(accountId: AccountId): Set<Medicine> {
        return medicineRepository.findByOwner(accountId)
    }

    fun findAllSharedGroupMedicines(accountId: AccountId): Set<Medicine> {
        val sharedGroup = findJoinedSharedGroup(accountId) ?: return emptySet()
        return medicineRepository.findByOwner(sharedGroup.id)
    }

    fun findAllMembersMedicines(accountId: AccountId): Set<Medicine> {
        val sharedGroup = findJoinedSharedGroup(accountId) ?: return emptySet()
        val members = sharedGroup.members - accountId
        return medicineRepository.findByOwners(members).filter { it.isPublic }.toSet()
    }

    fun findAvailableMedicine(medicineId: MedicineId, accountId: AccountId): Medicine? {
        val medicine = medicineRepository.findById(medicineId) ?: return null
        if (medicine.isOwnedBy(accountId)) return medicine

        val sharedGroup = findJoinedSharedGroup(accountId) ?: return null
        return if (medicine.isOwnedBy(sharedGroup.id)) medicine else null
    }

    fun findAllAvailableMedicines(accountId: AccountId): Set<Medicine> {
        val ownedMedicines = findAllOwnedMedicines(accountId)
        val sharedGroupMedicines = findAllSharedGroupMedicines(accountId)
        return ownedMedicines + sharedGroupMedicines
    }

    fun isAvailableMedicine(medicineId: MedicineId, accountId: AccountId): Boolean {
        return findAvailableMedicine(medicineId, accountId) != null
    }

    fun findViewableMedicine(medicineId: MedicineId, accountId: AccountId): Medicine? {
        val medicine = medicineRepository.findById(medicineId) ?: return null
        if (medicine.isOwnedBy(accountId)) return medicine

        val sharedGroup = findJoinedSharedGroup(accountId) ?: return null
        if (medicine.isOwnedBy(sharedGroup.id)) return medicine

        return if (medicine.isPublic && sharedGroup.members.contains(medicine.owner.accountId)) medicine else null
    }

    fun findAllViewableMedicines(accountId: AccountId): Set<Medicine> {
        val ownedMedicines = findAllOwnedMedicines(accountId)
        val sharedGroupMedicines = findAllSharedGroupMedicines(accountId)
        val membersMedicines = findAllMembersMedicines(accountId)
        return ownedMedicines + sharedGroupMedicines + membersMedicines
    }

    fun isViewableMedicine(medicineId: MedicineId, accountId: AccountId): Boolean {
        return findViewableMedicine(medicineId, accountId) != null
    }

    private fun findJoinedSharedGroup(accountId: AccountId) = sharedGroupRepository.findByMember(accountId)
}
