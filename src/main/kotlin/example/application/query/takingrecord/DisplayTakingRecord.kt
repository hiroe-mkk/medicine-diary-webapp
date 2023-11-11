package example.application.query.takingrecord

import example.application.query.shared.type.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import example.domain.shared.type.*
import java.time.*

data class DisplayTakingRecord(val takingRecordId: TakingRecordId,
                               val followUp: FollowUp,
                               val note: Note,
                               val takenAt: LocalDateTime,
                               val medicineId: MedicineId,
                               val medicineName: MedicineName,
                               val recorder: User)