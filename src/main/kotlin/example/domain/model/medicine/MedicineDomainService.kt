package example.domain.model.medicine

import example.domain.model.account.*
import example.domain.model.sharedgroup.*
import example.domain.shared.type.*
import org.springframework.stereotype.*
import java.time.*

@Component
class MedicineDomainService(private val medicineRepository: MedicineRepository,
                            private val sharedGroupRepository: SharedGroupRepository) {
    fun findOwnedMedicine(medicineId: MedicineId, accountId: AccountId): Medicine? {
        val medicine = medicineRepository.findById(medicineId) ?: return null
        return if (medicine.isOwnedBy(accountId)) medicine else null
    }

    fun findAllOwnedMedicines(accountId: AccountId): Set<Medicine> {
        return medicineRepository.findByAccountId(accountId)
    }

    fun findAllSharedGroupMedicines(accountId: AccountId): Set<Medicine> {
        val sharedGroup = findParticipatingSharedGroup(accountId) ?: return emptySet()
        return medicineRepository.findBySharedGroupId(sharedGroup.id)
    }

    fun findUserMedicine(medicineId: MedicineId, accountId: AccountId): Medicine? {
        val medicine = medicineRepository.findById(medicineId) ?: return null
        if (medicine.isOwnedBy(accountId)) return medicine

        val sharedGroup = findParticipatingSharedGroup(accountId) ?: return null
        return if (medicine.isOwnedBy(sharedGroup.id)) medicine else null
    }

    fun findAllUserMedicines(accountId: AccountId): Set<Medicine> {
        val ownedMedicines = findAllOwnedMedicines(accountId)
        val sharedGroupMedicines = findAllSharedGroupMedicines(accountId)
        return ownedMedicines + sharedGroupMedicines
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
        val sharedGroup = findParticipatingSharedGroup(accountId) ?: return emptySet()
        val sharedGroupMedicines = medicineRepository.findBySharedGroupId(sharedGroup.id)
        val members = sharedGroup.members - accountId
        val membersMedicines = medicineRepository.findByAccountIds(members).filter { it.isPublic }
        return ownedMedicines + sharedGroupMedicines + membersMedicines
    }

    fun createMedicine(id: MedicineId,
                       medicineName: MedicineName,
                       dosageAndAdministration: DosageAndAdministration,
                       effects: Effects,
                       precautions: Note,
                       isPublic: Boolean,
                       registeredAt: LocalDateTime,
                       registrant: AccountId,
                       isWantToOwn: Boolean): Medicine {
        val owner = if (isWantToOwn) {
            MedicineOwner.create(registrant)
        } else {
            findParticipatingSharedGroup(registrant)
                ?.let { MedicineOwner.create(it.id) }
            ?: MedicineOwner.create(registrant)
        }

        return Medicine(id,
                        owner,
                        medicineName,
                        dosageAndAdministration,
                        effects,
                        precautions,
                        null,
                        if (owner.isSharedGroup) true else isPublic,
                        registeredAt)
    }

    private fun findParticipatingSharedGroup(accountId: AccountId) = sharedGroupRepository.findByMember(accountId)
}