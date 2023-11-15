package example.application.query.medicationrecord

import com.fasterxml.jackson.annotation.*

data class JSONFollowUp(val symptom: String,
                        val beforeTaking: String,
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        val afterTaking: String?)