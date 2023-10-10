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
                                       medicineId: MedicineId,
                                       dose: Dose,
                                       symptoms: Symptoms,
                                       note: Note,
                                       val takenAt: LocalDateTime) {
    var medicineId: MedicineId = medicineId
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
                   medicine: Medicine,
                   dose: Dose,
                   symptoms: Symptoms,
                   note: Note,
                   takenAt: LocalDateTime): TakingRecord {
            if (medicine.owner != recorder) throw OperationNotPermittedException("このお薬の服用記録を追加することはできません。")

            return TakingRecord(takingRecordId, recorder, medicine.id, dose, symptoms, note, takenAt)
        }

        fun reconstruct(takingRecordId: TakingRecordId,
                        recorder: AccountId,
                        medicineId: MedicineId,
                        dose: Dose,
                        symptoms: Symptoms,
                        note: Note,
                        takenAt: LocalDateTime): TakingRecord {
            return TakingRecord(takingRecordId, recorder, medicineId, dose, symptoms, note, takenAt)
        }
    }

    fun isRecordedBy(accountId: AccountId): Boolean = recorder == accountId

    fun modify(medicine: Medicine, dose: Dose, symptoms: Symptoms, note: Note) {
        if (medicine.owner != recorder) throw OperationNotPermittedException("このお薬の服用記録に修正することはできません。")

        this.medicineId = medicine.id
        this.dose = dose
        this.symptoms = symptoms
        this.note = note
    }
}