package example.domain.model.medicine

import example.domain.model.account.*
import example.domain.shared.type.*
import org.springframework.stereotype.*
import java.time.*

@Component
class MedicineDomainService() {
    fun createMedicine(id: MedicineId,
                       medicineName: MedicineName,
                       dosageAndAdministration: DosageAndAdministration,
                       effects: Effects,
                       precautions: Note,
                       isPublic: Boolean,
                       registeredAt: LocalDateTime,
                       registrant: AccountId): Medicine {
        val owner = MedicineOwner(registrant, null) // TODO

        return Medicine(id,
                        owner,
                        medicineName,
                        dosageAndAdministration,
                        effects,
                        precautions,
                        null,
                        isPublic,
                        registeredAt)
    }
}