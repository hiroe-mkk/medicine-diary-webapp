package example.application.query.takingrecord

import example.application.query.shared.type.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import example.domain.shared.type.*
import java.time.*

data class TakingRecordDetail(val takingRecordId: TakingRecordId,
                              val medicineId: MedicineId,
                              val name: String,
                              val dose: Dose,
                              val takingUnit: String,
                              val followUp: FollowUp,
                              val note: Note,
                              val takenAt: LocalDateTime,
                              val recorder: User)