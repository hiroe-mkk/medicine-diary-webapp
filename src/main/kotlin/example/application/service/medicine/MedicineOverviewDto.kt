package example.application.service.medicine

import example.domain.model.medicine.*
import example.domain.model.medicine.medicineimage.*

data class MedicineOverviewDto(val medicineId: MedicineId,
                               val medicineName: MedicineName,
                               val dosageAndAdministration: DosageAndAdministration,
                               val medicineImageURL: MedicineImageURL?,
                               val effects: Effects) {
    companion object {
        fun from(medicine: Medicine): MedicineOverviewDto {
            return MedicineOverviewDto(medicine.id,
                                       medicine.medicineName,
                                       medicine.dosageAndAdministration,
                                       medicine.medicineImageURL,
                                       medicine.effects)
        }
    }
}