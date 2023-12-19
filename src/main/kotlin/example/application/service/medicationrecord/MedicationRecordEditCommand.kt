package example.application.service.medicationrecord

import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import example.domain.shared.type.*
import example.domain.shared.validation.*
import jakarta.validation.constraints.*
import org.springframework.format.annotation.*
import java.time.*
import java.time.temporal.*

/**
 * 服用記録の追加と修正に利用される Command クラス
 */
data class MedicationRecordEditCommand(@field:NotWhitespaceOnly(message = "※お薬を選択してください。")
                                       val takenMedicine: String,
                                       @field:NotNull(message = "※服用した量を入力してください。")
                                       @field:MedicineQuantity
                                       val quantity: Double?,
                                       @field:NotWhitespaceOnly(message = "※症状を入力してください。")
                                       @field:Size(max = 30,
                                                   message = "※{max}文字以内で入力してください。")
                                       val symptom: String,
                                       @field:NotNull(message = "※服用前の症状の度合いを入力してください。")
                                       val beforeMedication: ConditionLevel?,
                                       val afterMedication: ConditionLevel?,
                                       @field:Size(max = 500, message = "※{max}文字以内で入力してください。")
                                       val note: String,
                                       @field:NotNull(message = "※服用した日付を入力してください。")
                                       @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                       val takenMedicineOn: LocalDate?,
                                       @field:NotNull(message = "※服用した時間を入力してください。")
                                       @field:DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
                                       val takenMedicineAt: LocalTime?) {
    val validatedTakenMedicine: MedicineId = MedicineId(takenMedicine)
    val validatedDose: Dose = Dose(quantity ?: 0.0)
    val validFollowUp: FollowUp = FollowUp(symptom, beforeMedication ?: ConditionLevel.A_LITTLE_BAD, afterMedication)
    val validatedNote: Note = Note(note.trim())
    val validatedTakenMedicineOn: LocalDate = takenMedicineOn ?: LocalDate.now()
    val validatedTakenMedicineAt: LocalTime = takenMedicineAt?.truncatedTo(ChronoUnit.MINUTES)
                                              ?: LocalTime.now().truncatedTo(ChronoUnit.MINUTES)

    companion object {
        fun initialize(medicineId: MedicineId? = null,
                       date: LocalDate? = null): MedicationRecordEditCommand {
            return MedicationRecordEditCommand(medicineId?.value ?: "",
                                               0.0,
                                               "",
                                               ConditionLevel.A_LITTLE_BAD,
                                               null,
                                               "",
                                               date ?: LocalDate.now(),
                                               LocalTime.of(0, 0))
        }

        fun initialize(medicationRecord: MedicationRecord): MedicationRecordEditCommand {
            return MedicationRecordEditCommand(medicationRecord.takenMedicine.value,
                                               medicationRecord.dose.quantity,
                                               medicationRecord.followUp.symptom,
                                               medicationRecord.followUp.beforeMedication,
                                               medicationRecord.followUp.afterMedication,
                                               medicationRecord.note.value,
                                               medicationRecord.takenMedicineOn,
                                               medicationRecord.takenMedicineAt)
        }
    }
}