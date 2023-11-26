package example.domain.model.medicine

import example.domain.model.account.*
import example.domain.shared.type.*
import org.springframework.stereotype.*
import java.time.*

@Component
class MedicineCreationService(private val medicineOwnerCreationService: MedicineOwnerCreationService) {
    fun create(id: MedicineId,
               medicineName: MedicineName,
               dosageAndAdministration: DosageAndAdministration,
               effects: Effects,
               precautions: Note,
               isOwnedBySharedGroup: Boolean,
               isPublic: Boolean,
               registeredAt: LocalDateTime,
               registrant: AccountId): Medicine {
        val medicineOwner = if (isOwnedBySharedGroup) {
            medicineOwnerCreationService.createSharedGroupOwner(registrant)
        } else {
            medicineOwnerCreationService.createAccountOwner(registrant)
        }

        return Medicine(id,
                        medicineOwner,
                        medicineName,
                        dosageAndAdministration,
                        effects,
                        precautions,
                        null,
                        if (medicineOwner.isSharedGroup) true else isPublic,
                        null,
                        registeredAt)
    }
}