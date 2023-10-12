package example.application.service.takingrecord

import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import example.domain.shared.type.*
import jakarta.validation.constraints.*
import org.springframework.format.annotation.*
import java.time.*
import java.time.temporal.*

/**
 * 服用記録の追加と修正に利用される Command クラス
 */
data class TakingRecordEditCommand(@field:NotEmpty(message = "※必ず選択してください。")
                                   val takenMedicine: String,
                                   @field:NotNull(message = "※必ず入力してください。")
                                   @field:Digits(integer = 5, fraction = 3,
                                                 message = "※整数{integer}桁、小数点以下{fraction}桁の範囲で入力してください。")
                                   @field:DecimalMin(value = "0.001", message = "※{value}以上の数値を入力してください。")
                                   val quantity: Double?,
                                   @field:NotEmpty(message = "※必ず入力してください。")
                                   @field:Size(max = 30,
                                               message = "※{max}文字以内で入力してください。")
                                   val symptom: String,
                                   @field:NotNull(message = "※必ず入力してください。")
                                   val beforeTaking: ConditionLevel?,
                                   val afterTaking: ConditionLevel?,
                                   @field:Size(max = 500, message = "※{max}文字以内で入力してください。")
                                   val note: String,
                                   @field:NotNull(message = "※必ず入力してください。")
                                   @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                   val takenAt: LocalDateTime?) {
    val validatedTakenMedicine: MedicineId = MedicineId(takenMedicine)
    val validatedDose: Dose = Dose(quantity ?: 0.0)
    val validFollowUp: FollowUp = FollowUp(symptom, beforeTaking ?: ConditionLevel.A_LITTLE_BAD, afterTaking)
    val validatedNote: Note = Note(note.trim())
    val validatedTakenAt: LocalDateTime = takenAt?.truncatedTo(ChronoUnit.MINUTES)
                                          ?: LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)

    companion object {
        fun initialize(): TakingRecordEditCommand {
            return TakingRecordEditCommand("",
                                           0.0,
                                           "",
                                           ConditionLevel.A_LITTLE_BAD,
                                           null,
                                           "",
                                           LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
        }

        fun initialize(takingRecord: TakingRecord): TakingRecordEditCommand {
            return TakingRecordEditCommand(takingRecord.takenMedicine.value,
                                           takingRecord.dose.quantity,
                                           takingRecord.followUp.symptom,
                                           takingRecord.followUp.beforeTaking,
                                           takingRecord.followUp.afterTaking,
                                           takingRecord.note.value,
                                           takingRecord.takenAt)
        }
    }
}