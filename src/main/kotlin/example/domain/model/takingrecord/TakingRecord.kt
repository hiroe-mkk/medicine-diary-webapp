package example.domain.model.takingrecord

import example.domain.model.account.*
import example.domain.model.medicine.*
import example.domain.shared.type.*
import java.time.*

/**
 * 服用記録
 */
class TakingRecord private constructor(val id: TakingRecordId,
                                       val recorder: AccountId,
                                       takenMedicine: MedicineId,
                                       dose: Dose,
                                       followUp: FollowUp,
                                       note: Note,
                                       takenAt: LocalDateTime) {
    var takenMedicine: MedicineId = takenMedicine
        private set
    var dose: Dose = dose
        private set
    var followUp: FollowUp = followUp
        private set
    var note: Note = note
        private set
    var takenAt: LocalDateTime = takenAt
        private set

    companion object {
        fun create(takingRecordId: TakingRecordId,
                   recorder: AccountId,
                   takenMedicine: Medicine,
                   dose: Dose,
                   followUp: FollowUp,
                   note: Note,
                   takenAt: LocalDateTime): TakingRecord {
            return TakingRecord(takingRecordId, recorder, takenMedicine.id, dose, followUp, note, takenAt)
        }

        fun reconstruct(takingRecordId: TakingRecordId,
                        recorder: AccountId,
                        takenMedicine: MedicineId,
                        dose: Dose,
                        followUp: FollowUp,
                        note: Note,
                        takenAt: LocalDateTime): TakingRecord {
            return TakingRecord(takingRecordId, recorder, takenMedicine, dose, followUp, note, takenAt)
        }
    }

    fun isRecordedBy(accountId: AccountId): Boolean = recorder == accountId

    fun modify(takenMedicine: Medicine,
               dose: Dose,
               followUp: FollowUp,
               note: Note,
               takenAt: LocalDateTime) {
        this.takenMedicine = takenMedicine.id
        this.dose = dose
        this.followUp = followUp
        this.note = note
        this.takenAt = takenAt
    }
}