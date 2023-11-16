package example.application.query.medicationrecord

import com.fasterxml.jackson.annotation.*

data class JSONFollowUp(val symptom: String,
                        val beforeMedication: String,
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        val afterMedication: String?)