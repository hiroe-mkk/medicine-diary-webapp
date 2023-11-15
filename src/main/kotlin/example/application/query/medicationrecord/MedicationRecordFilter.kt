package example.application.query.medicationrecord

import example.domain.model.account.*
import example.domain.model.medicine.*
import java.time.*

data class MedicationRecordFilter(val medicineid: MedicineId?,
                                  val accountids: Set<AccountId> = emptySet(),
                                  val start: LocalDate?,
                                  val end: LocalDate?)