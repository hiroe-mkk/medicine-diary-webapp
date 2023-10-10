package example.domain.model.takingrecord

import example.domain.model.account.*
import example.domain.model.medicine.*
import example.domain.shared.exception.*
import example.domain.shared.type.*
import java.time.*

/**
 * 服用記録
 */
class TakingRecord private constructor(val id: TakingRecordId,
                                       val recorder: AccountId,
                                       takenMedicine: MedicineId,
                                       dose: Dose,
                                       symptoms: Symptoms,
                                       note: Note,
                                       val takenAt: LocalDateTime) {
    var takenMedicine: MedicineId = takenMedicine
        private set
    var dose: Dose = dose
        private set
    var symptoms: Symptoms = symptoms
        private set
    var note: Note = note
        private set

    companion object {
        fun create(takingRecordId: TakingRecordId,
                   recorder: AccountId,
                   takenMedicine: Medicine,
                   dose: Dose,
                   symptoms: Symptoms,
                   note: Note,
                   takenAt: LocalDateTime): TakingRecord {
            if (takenMedicine.owner != recorder) throw OperationNotPermittedException("このお薬の服用記録を追加することはできません。")

            return TakingRecord(takingRecordId, recorder, takenMedicine.id, dose, symptoms, note, takenAt)
        }

        fun reconstruct(takingRecordId: TakingRecordId,
                        recorder: AccountId,
                        takenMedicine: MedicineId,
                        dose: Dose,
                        symptoms: Symptoms,
                        note: Note,
                        takenAt: LocalDateTime): TakingRecord {
            return TakingRecord(takingRecordId, recorder, takenMedicine, dose, symptoms, note, takenAt)
        }
    }

    fun isRecordedBy(accountId: AccountId): Boolean = recorder == accountId

    fun modify(takenMedicine: Medicine, dose: Dose, symptoms: Symptoms, note: Note) {
        if (takenMedicine.owner != recorder) throw OperationNotPermittedException("このお薬の服用記録に修正することはできません。")

        this.takenMedicine = takenMedicine.id
        this.dose = dose
        this.symptoms = symptoms
        this.note = note
    }
}