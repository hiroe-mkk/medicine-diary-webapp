package example.presentation.controller.api.medicine

import com.fasterxml.jackson.annotation.*
import example.application.service.medicine.*
import example.domain.model.medicine.*
import example.presentation.controller.api.takingrecord.*

class JSONMedicineOverviewsResponse(val medicines: List<JSONMedicineOverviews>) {
    companion object {
        fun from(medicines: List<MedicineOverviewDto>): JSONMedicineOverviewsResponse {
            val jsonMedicineOverviews = medicines.map {
                JSONMedicineOverviews(it.medicineId.value,
                                      it.medicineName.value,
                                      JSONDosageAndAdministration.from(it.dosageAndAdministration),
                                      it.medicineImageURL?.toURL(),
                                      it.effects.values)
            }
            return JSONMedicineOverviewsResponse(jsonMedicineOverviews)
        }
    }

    class JSONMedicineOverviews(val medicineId: String,
                                val medicineName: String,
                                val dosageAndAdministration: JSONDosageAndAdministration,
                                @JsonInclude(JsonInclude.Include.NON_NULL)
                                val medicineImageURL: String?,
                                val effects: List<String>) {
    }

    class JSONDosageAndAdministration(val quantity: String,
                                      val doseUnit: String,
                                      val timesPerDay: String,
                                      val timingOptions: List<String>) {
        companion object {
            fun from(dosageAndAdministration: DosageAndAdministration): JSONDosageAndAdministration {
                return JSONDosageAndAdministration(dosageAndAdministration.dose.toString(),
                                                   dosageAndAdministration.doseUnit,
                                                   dosageAndAdministration.timesPerDay.toString(),
                                                   dosageAndAdministration.timingOptions.map { it.str })
            }
        }
    }
}