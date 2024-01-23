package example.application.query.medicine

import com.fasterxml.jackson.annotation.*
import example.domain.model.medicine.*
import example.infrastructure.repository.shared.*

class JSONMedicineOverview(val medicineId: String,
                           val medicineName: String,
                           @JsonInclude(JsonInclude.Include.NON_NULL)
                           val medicineImageURL: String?,
                           val isPublic: Boolean) {
    private lateinit var dosageAndAdministrationForMapping: JSONDosageAndAdministration
    val dosageAndAdministration: JSONDosageAndAdministration
        get() = dosageAndAdministrationForMapping

    private lateinit var effectsForMapping: List<String>
    val effects: List<String>
        get() = effectsForMapping

    // テスト用コンストラクタ
    constructor(medicineId: String,
                medicineName: String,
                medicineImageURL: String?,
                isPublic: Boolean,
                dosageAndAdministration: JSONDosageAndAdministration,
                effects: List<String>) : this(medicineId, medicineName, medicineImageURL, isPublic) {
        this.dosageAndAdministrationForMapping = dosageAndAdministration
        this.effectsForMapping = effects
    }
}