package example.application.service.medicine

import example.domain.model.medicine.*

data class MedicineOverviewDto(val medicineId: MedicineId,
                               val name: String,
                               val takingUnit: String,
                               val dosage: Dosage,
                               val administration: Administration,
                               val effects: Effects) {
    companion object {
        fun from(medicine: Medicine): MedicineOverviewDto {
            return MedicineOverviewDto(medicine.id,
                                       medicine.name,
                                       medicine.takingUnit,
                                       medicine.dosage,
                                       medicine.administration,
                                       medicine.effects)
        }
    }
}