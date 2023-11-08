package example.application.service.medicine

import example.domain.model.medicine.*
import example.domain.model.medicine.medicineImage.*
import example.domain.shared.type.*
import java.time.*

data class MedicineDto(val medicineId: MedicineId,
                       val owner: MedicineOwner,
                       val medicineName: MedicineName,
                       val dosageAndAdministration: DosageAndAdministration,
                       val effects: Effects,
                       val precautions: Note,
                       val medicineImageURL: MedicineImageURL?,
                       val isPublic: Boolean,
                       val registeredAt: LocalDateTime) {
    companion object {
        fun from(medicine: Medicine): MedicineDto {
            return MedicineDto(medicine.id,
                               medicine.owner,
                               medicine.medicineName,
                               medicine.dosageAndAdministration,
                               medicine.effects,
                               medicine.precautions,
                               medicine.medicineImageURL,
                               medicine.isPublic,
                               medicine.registeredAt)
        }
    }
}