package example.application.service.medicine

import example.domain.model.medicine.*
import example.domain.model.medicine.medicineImage.*

data class MedicineOverviewDto(val medicineId: MedicineId,
                               val name: String,
                               val dosage: Dosage,
                               val administration: Administration,
                               val medicineImageURL: MedicineImageURL?,
                               val effects: Effects) {
    companion object {
        fun from(medicine: Medicine): MedicineOverviewDto {
            return MedicineOverviewDto(medicine.id,
                                       medicine.name,
                                       medicine.dosage,
                                       medicine.administration,
                                       medicine.medicineImageURL,
                                       medicine.effects)
        }
    }
}