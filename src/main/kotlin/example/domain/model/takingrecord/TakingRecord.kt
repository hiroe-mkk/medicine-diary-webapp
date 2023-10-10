package example.domain.model.takingrecord

import example.domain.model.account.*
import example.domain.model.medicine.*
import example.domain.shared.type.*
import java.time.*

/**
 * 服用記録
 */
class TakingRecord(val id: TakingRecordId,
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

    fun modify(newMedicineId: MedicineId,
               newDose: Dose,
               newSymptoms: Symptoms,
               newNote: Note) {
        this.medicineId = newMedicineId
        this.dose = newDose
        this.symptoms = newSymptoms
        this.note = newNote
    }
}