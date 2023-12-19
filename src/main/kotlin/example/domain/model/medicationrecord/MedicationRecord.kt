package example.domain.model.medicationrecord

import example.domain.model.account.*
import example.domain.model.medicine.*
import example.domain.shared.type.*
import java.time.*

/**
 * 服用記録
 */
class MedicationRecord(val id: MedicationRecordId,
                       val recorder: AccountId,
                       takenMedicine: MedicineId,
                       dose: Dose,
                       followUp: FollowUp,
                       note: Note,
                       takenMedicineOn: LocalDate,
                       takenMedicineAt: LocalTime) {
    var takenMedicine: MedicineId = takenMedicine
        private set
    var dose: Dose = dose
        private set
    var followUp: FollowUp = followUp
        private set
    var note: Note = note
        private set
    var takenMedicineOn: LocalDate = takenMedicineOn
        private set
    var takenMedicineAt: LocalTime = takenMedicineAt
        private set

    fun isRecordedBy(accountId: AccountId): Boolean = recorder == accountId

    fun changeAttributes(takenMedicine: MedicineId,
                         dose: Dose,
                         followUp: FollowUp,
                         note: Note,
                         takenMedicineOn: LocalDate,
                         takenMedicineAt: LocalTime) {
        this.takenMedicine = takenMedicine
        this.dose = dose
        this.followUp = followUp
        this.note = note
        this.takenMedicineOn = takenMedicineOn
        this.takenMedicineAt = takenMedicineAt
    }

    fun changeTakenMedicine(takenMedicine: MedicineId) {
        this.takenMedicine = takenMedicine
    }
}