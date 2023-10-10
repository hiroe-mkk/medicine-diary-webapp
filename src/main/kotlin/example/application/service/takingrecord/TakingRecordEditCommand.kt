package example.application.service.takingrecord

import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import example.domain.shared.type.*
import jakarta.validation.*
import jakarta.validation.constraints.*

/**
 * 服用記録の追加と修正に利用される Command クラス
 */
data class TakingRecordEditCommand(@field:NotEmpty(message = "※必ず入力してください。")
                                   val takenMedicine: String,
                                   @field:NotNull(message = "※必ず入力してください。")
                                   @field:Digits(integer = 5, fraction = 3,
                                                 message = "※整数{integer}桁、小数点以下{fraction}桁の範囲で入力してください。")
                                   @field:DecimalMin(value = "0.001", message = "※{value}以上の数値を入力してください。")
                                   val quantity: Double?,
                                   @field:Valid
                                   val symptoms: List<@Valid FollowUpInputField> = emptyList(),
                                   @field:Size(max = 500, message = "※{max}文字以内で入力してください。")
                                   val note: String) {
    val validatedTakenMedicine: MedicineId = MedicineId(takenMedicine)
    val validatedDose: Dose = Dose(quantity ?: 0.0)
    val validSymptoms: Symptoms = Symptoms(symptoms.map { it.toFollowUp() })
    val validatedNote: Note = Note(note.trim())

    data class FollowUpInputField(@field:Size(max = 30,
                                              message = "※{max}文字以内で入力してください。")
                                  val symptom: String,
                                  @field:NotNull
                                  val beforeTaking: ConditionLevel?,
                                  val afterTaking: ConditionLevel?) {
        fun toFollowUp(): FollowUp = FollowUp(symptom, beforeTaking ?: ConditionLevel.NOT_BAD, afterTaking)
    }
}