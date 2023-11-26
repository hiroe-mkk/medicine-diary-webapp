package example.domain.model.medicine

import example.domain.model.account.*
import example.domain.model.sharedgroup.*
import example.domain.shared.type.*
import org.springframework.stereotype.*
import java.time.*

@Component
class MedicineCreationService(private val sharedGroupQueryService: SharedGroupQueryService) {
    fun create(id: MedicineId,
               medicineName: MedicineName,
               dosageAndAdministration: DosageAndAdministration,
               effects: Effects,
               precautions: Note,
               isOwnedBySharedGroup: Boolean,
               isPublic: Boolean,
               registeredAt: LocalDateTime,
               registrant: AccountId): Medicine {
        val owner = if (isOwnedBySharedGroup) {
            val participatingSharedGroup = sharedGroupQueryService.findParticipatingSharedGroup(registrant)
            if (participatingSharedGroup != null) {
                MedicineOwner.create(participatingSharedGroup.id)
            } else {
                MedicineOwner.create(registrant)
            }
        } else {
            MedicineOwner.create(registrant)
        }

        return Medicine(id,
                        owner,
                        medicineName,
                        dosageAndAdministration,
                        effects,
                        precautions,
                        null,
                        if (owner.isSharedGroup) true else isPublic,
                        null,
                        registeredAt)
    }
}