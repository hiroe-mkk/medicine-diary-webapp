package example.application.service.medicine

import example.domain.model.medicine.*
import java.time.*

data class MedicineDetailDto(val medicineId: MedicineId,
                             val name: String,
                             val takingUnit: String,
                             val dosage: Dosage,
                             val administration: Administration,
                             val effects: Effects,
                             val precautions: String,
                             val registeredAt: LocalDateTime) {
    companion object {
        fun from(medicine: Medicine): MedicineDetailDto {
            return MedicineDetailDto(medicine.id,
                                     medicine.name,
                                     medicine.takingUnit,
                                     medicine.dosage,
                                     medicine.administration,
                                     medicine.effects,
                                     medicine.precautions,
                                     medicine.registeredAt)
        }
    }
}