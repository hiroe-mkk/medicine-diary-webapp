package example.domain.model.medicine

import example.domain.model.account.*
import example.domain.model.sharedgroup.*
import example.domain.shared.type.*
import org.springframework.stereotype.*
import java.time.*

@Component
class MedicineDomainService(private val sharedGroupRepository: SharedGroupRepository) {
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
            sharedGroupRepository.findByMember(registrant)
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
}