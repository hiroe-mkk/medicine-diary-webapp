package example.application.query.medicationrecord

import example.domain.model.account.*
import example.domain.model.medicine.*
import java.time.*

data class MedicationRecordFilter(val medicine: MedicineId?,
                                  val account: AccountId?,
                                  val start: LocalDate?,
                                  val end: LocalDate?)