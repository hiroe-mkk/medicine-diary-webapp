package example.domain.model.medicine

import example.domain.model.account.*
import example.domain.model.sharedgroup.*
import org.springframework.stereotype.*

@Component
class MedicineQueryService(private val medicineRepository: MedicineRepository,
                           private val sharedGroupRepository: SharedGroupRepository) {
    fun findOwnedMedicine(medicineId: MedicineId, accountId: AccountId): Medicine? {
        val medicine = medicineRepository.findById(medicineId) ?: return null
        return if (medicine.isOwnedBy(accountId)) medicine else null
    }

    fun findAllOwnedMedicines(accountId: AccountId): Set<Medicine> {
        return medicineRepository.findByAccountId(accountId)
    }

    fun isOwnedMedicine(medicineId: MedicineId, accountId: AccountId): Boolean {
        return findOwnedMedicine(medicineId, accountId) != null
    }

    fun findAllSharedGroupMedicines(accountId: AccountId): Set<Medicine> {
        val sharedGroup = findParticipatingSharedGroup(accountId) ?: return emptySet()
        return medicineRepository.findBySharedGroupId(sharedGroup.id)
    }

    fun findAllMembersMedicines(accountId: AccountId): Set<Medicine> {
        val sharedGroup = findParticipatingSharedGroup(accountId) ?: return emptySet()
        val members = sharedGroup.members - accountId
        return medicineRepository.findByAccountIds(members).filter { it.isPublic }.toSet()
    }

    fun findAvailableMedicine(medicineId: MedicineId, accountId: AccountId): Medicine? {
        val medicine = medicineRepository.findById(medicineId) ?: return null
        if (medicine.isOwnedBy(accountId)) return medicine

        val sharedGroup = findParticipatingSharedGroup(accountId) ?: return null
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

        val sharedGroup = findParticipatingSharedGroup(accountId) ?: return null
        if (medicine.isOwnedBy(sharedGroup.id)) return medicine

        return if (medicine.isPublic && sharedGroup.members.contains(medicine.owner.accountId)) medicine else null
    }

    fun isViewableMedicine(medicineId: MedicineId, accountId: AccountId): Boolean {
        return findViewableMedicine(medicineId, accountId) != null
    }

    fun findAllViewableMedicines(accountId: AccountId): Set<Medicine> {
        val ownedMedicines = findAllOwnedMedicines(accountId)
        val sharedGroup = findParticipatingSharedGroup(accountId) ?: return ownedMedicines
        val sharedGroupMedicines = medicineRepository.findBySharedGroupId(sharedGroup.id)
        val members = sharedGroup.members - accountId
        val membersMedicines = medicineRepository.findByAccountIds(members).filter { it.isPublic }
        return ownedMedicines + sharedGroupMedicines + membersMedicines
    }

    private fun findParticipatingSharedGroup(accountId: AccountId) = sharedGroupRepository.findByMember(accountId)
}