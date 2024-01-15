package example.application.query.medicationrecord

import com.fasterxml.jackson.annotation.*
import example.application.query.user.*

class JSONMedicationRecord(val medicationRecordId: String,
                           val takenMedicine: JSONTakenMedicine,
                           val followUp: JSONFollowUp,
                           val note: String,
                           val takenMedicineOn: String,
                           val takenMedicineAt: String,
                           @JsonInclude(JsonInclude.Include.NON_NULL)
                           val symptomOnsetAt: String?,
                           @JsonInclude(JsonInclude.Include.NON_NULL)
                           val onsetEffectAt: String?,
                           val recorder: JSONUser,
                           val isRecordedBySelf: Boolean)