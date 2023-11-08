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

    fun findUserMedicine(medicineId: MedicineId, accountId: AccountId): Medicine? {
        val medicine = medicineRepository.findById(medicineId) ?: return null
        if (medicine.isOwnedBy(accountId)) return medicine

        val sharedGroup = findParticipatingSharedGroup(accountId) ?: return null
        return if (medicine.isOwnedBy(sharedGroup.id)) medicine else null
    }

    fun findAllUserMedicines(accountId: AccountId): List<Medicine> {
        val ownedMedicines = medicineRepository.findByAccountId(accountId)
        val sharedGroupMedicines = findParticipatingSharedGroup(accountId)
                                       ?.let {
                                           medicineRepository.findBySharedGroupId(it.id)
                                       } ?: emptyList()
        return ownedMedicines + sharedGroupMedicines
    }

    fun findViewableMedicine(medicineId: MedicineId, accountId: AccountId): Medicine? {
        val medicine = medicineRepository.findById(medicineId) ?: return null
        if (medicine.isOwnedBy(accountId)) return medicine

        val sharedGroup = findParticipatingSharedGroup(accountId) ?: return null
        if (medicine.isOwnedBy(sharedGroup.id)) return medicine

        return if (medicine.isPublic && sharedGroup.members.contains(medicine.owner.accountId)) medicine else null
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