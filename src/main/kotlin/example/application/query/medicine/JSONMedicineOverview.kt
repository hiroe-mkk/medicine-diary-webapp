package example.application.query.medicine

import com.fasterxml.jackson.annotation.*
import example.domain.model.medicine.*
import example.infrastructure.repository.shared.*

class JSONMedicineOverview(val medicineId: String,
                           val medicineName: String,
                           @JsonInclude(JsonInclude.Include.NON_NULL)
                           val medicineImageURL: String?) {
    private lateinit var dosageAndAdministrationForMapping: JSONDosageAndAdministration
    val dosageAndAdministration: JSONDosageAndAdministration
        get() = dosageAndAdministrationForMapping

    private lateinit var effectsForMapping: List<String>
    val effects: List<String>
        get() = effectsForMapping
}