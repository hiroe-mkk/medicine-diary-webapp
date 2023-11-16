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
                                       @field:Digits(integer = 5, fraction = 3,
                                                     message = "※整数{integer}桁、小数点以下{fraction}桁の範囲で入力してください。")
                                       @field:DecimalMin(value = "0.001", message = "※{value}以上の数値を入力してください。")
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
                                       @field:NotNull(message = "※服用した時間を入力してください。")
                                       @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                       val takenAt: LocalDateTime?) {
    val validatedTakenMedicine: MedicineId = MedicineId(takenMedicine)
    val validatedDose: Dose = Dose(quantity ?: 0.0)
    val validFollowUp: FollowUp = FollowUp(symptom, beforeMedication ?: ConditionLevel.A_LITTLE_BAD, afterMedication)
    val validatedNote: Note = Note(note.trim())
    val validatedTakenAt: LocalDateTime = takenAt?.truncatedTo(ChronoUnit.MINUTES)
                                          ?: LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)

    companion object {
        fun initialize(medicineId: MedicineId? = null): MedicationRecordEditCommand {
            return MedicationRecordEditCommand(medicineId?.value ?: "",
                                               0.0,
                                               "",
                                               ConditionLevel.A_LITTLE_BAD,
                                               null,
                                               "",
                                               LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
        }

        fun initialize(medicationRecord: MedicationRecord): MedicationRecordEditCommand {
            return MedicationRecordEditCommand(medicationRecord.takenMedicine.value,
                                               medicationRecord.dose.quantity,
                                               medicationRecord.followUp.symptom,
                                               medicationRecord.followUp.beforeMedication,
                                               medicationRecord.followUp.afterMedication,
                                               medicationRecord.note.value,
                                               medicationRecord.takenAt)
        }
    }
}