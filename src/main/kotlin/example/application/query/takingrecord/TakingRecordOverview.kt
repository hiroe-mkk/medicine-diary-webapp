package example.application.query.takingrecord

import example.application.query.shared.type.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import java.time.*

data class TakingRecordOverview(val takingRecordId: TakingRecordId,
                                val beforeTaking: ConditionLevel,
                                val afterTaking: ConditionLevel?,
                                val takenAt: LocalDateTime,
                                val medicineId: MedicineId,
                                val medicineName: String,
                                val recorder: User)