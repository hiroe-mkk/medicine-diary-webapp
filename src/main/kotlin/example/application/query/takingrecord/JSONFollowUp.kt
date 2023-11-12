package example.application.query.takingrecord

import com.fasterxml.jackson.annotation.*

data class JSONFollowUp(val symptom: String,
                        val beforeTaking: String,
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        val afterTaking: String?)