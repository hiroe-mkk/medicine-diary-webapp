package example.application.service.medicine

import example.domain.model.medicine.*
import example.domain.model.medicine.medicineImage.*
import example.domain.shared.type.*
import java.time.*

data class MedicineDetailDto(val medicineId: MedicineId,
                             val medicineName: MedicineName,
                             val dosageAndAdministration: DosageAndAdministration,
                             val effects: Effects,
                             val precautions: Note,
                             val medicineImageURL: MedicineImageURL?,
                             val isPublic: Boolean,
                             val registeredAt: LocalDateTime) {
    companion object {
        fun from(medicine: Medicine): MedicineDetailDto {
            return MedicineDetailDto(
                    medicine.id,
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