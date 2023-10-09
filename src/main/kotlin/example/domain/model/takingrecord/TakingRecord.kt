package example.domain.model.takingrecord

import example.domain.model.account.*
import example.domain.model.medicine.*
import example.domain.shared.type.*
import java.time.*

/**
 * 服用記録
 */
class TakingRecord(val id: TakingRecordId,
                   val accountId: AccountId,
                   val medicineId: MedicineId,
                   val dose: Dose,
                   val symptoms: Symptoms,
                   val note: Note,
                   val takenAt: LocalDateTime)