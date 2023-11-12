package example.application.query.takingrecord

import example.domain.model.account.*
import example.domain.model.medicine.*
import java.time.*

data class TakingRecordFilter(val medicineid: MedicineId?,
                              val accountids: Set<AccountId> = emptySet(),
                              val start: LocalDate?,
                              val end: LocalDate?)