package example.application.service.medicine

import example.domain.model.medicine.*
import example.domain.model.medicine.medicineImage.*
import example.domain.shared.type.*
import java.time.*

data class MedicineDetailDto(val medicineId: MedicineId,
                             val name: String,
                             val dosageAndAdministration: DosageAndAdministration,
                             val effects: Effects,
                             val precautions: Note,
                             val medicineImageURL: MedicineImageURL?,
                             val registeredAt: LocalDateTime) {
    companion object {
        fun from(medicine: Medicine): MedicineDetailDto {
            return MedicineDetailDto(medicine.id,
                                     medicine.name,
                                     medicine.dosageAndAdministration,
                                     medicine.effects,
                                     medicine.precautions,
                                     medicine.medicineImageURL,
                                     medicine.registeredAt)
        }
    }
}