package example.application.query.takingrecord

import example.domain.model.account.*
import example.domain.model.medicine.*
import java.time.*

data class TakingRecordOverviewsFilter(val medicine: MedicineId?,
                                       val members: Set<AccountId> = emptySet(),
                                       val start: LocalDate?,
                                       val end: LocalDate?)