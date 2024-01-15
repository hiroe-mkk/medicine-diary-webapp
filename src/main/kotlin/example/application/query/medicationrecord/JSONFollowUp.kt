package example.application.query.medicationrecord

import com.fasterxml.jackson.annotation.*
import example.domain.model.medicationrecord.*

class JSONFollowUp(val symptom: String,
                   private val beforeMedicationForMapping: ConditionLevel,
                   private val afterMedicationForMapping: ConditionLevel?) {
    val beforeMedication: String
        get() = beforeMedicationForMapping.str

    val afterMedication: String?
        @JsonInclude(JsonInclude.Include.NON_NULL)
        get() = afterMedicationForMapping?.str
}