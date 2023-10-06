package example.application.service.medicine

import example.domain.model.medicine.*
import jakarta.validation.*
import jakarta.validation.constraints.*

class MedicineBasicInfoInputCommand(@field:NotEmpty(message = "※必ず入力してください。")
                                    @field:Size(max = 30, message = "※{max}文字以内で入力してください。")
                                    private val name: String,
                                    @field:NotEmpty(message = "※必ず入力してください。")
                                    @field:Size(max = 10, message = "※{max}文字以内で入力してください。")
                                    private val takingUnit: String,
                                    @field:NotNull(message = "※必ず入力してください。")
                                    @field:Min(value = 0, message = "※{value}以上の数値を入力してください。")
                                    private val quantity: Double = 0.0,
                                    @field:NotNull(message = "※必ず入力してください。")
                                    @field:Min(value = 1, message = "※{value}以上の数値を入力してください。")
                                    private val timesPerDay: Int = 0,
                                    private val timingOptions: List<Timing> = emptyList(),
                                    @field:Valid
                                    private val effects: List<@Valid Effect> = emptyList(),
                                    @field:NotEmpty(message = "※必ず入力してください。")
                                    @field:Size(max = 500, message = "※{max}文字以内で入力してください。")
                                    private val precautions: String) {
    val validatedName: String = name.trim()
    val validatedTakingUnit: String = takingUnit.trim()
    val validatedDosage: Dosage = Dosage(quantity)
    val validatedAdministration: Administration = Administration(timesPerDay, timingOptions)
    val validatedEffects: Effects = Effects(effects.map(Effect::value))
    val validatedPrecautions: String = precautions.trim()

    // List のバリデーションを行うために必要
    data class Effect(@field:Size(max = 30,
                                  message = "※{max}文字以内で入力してください。") val value: String) {
        override fun toString(): String = value
    }
}