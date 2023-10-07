package example.application.service.medicine

import example.domain.model.medicine.*
import example.domain.shared.type.*
import jakarta.validation.*
import jakarta.validation.constraints.*

data class MedicineBasicInfoInputCommand(@field:NotEmpty(message = "※必ず入力してください。")
                                         @field:Size(max = 30, message = "※{max}文字以内で入力してください。")
                                         val name: String,
                                         @field:NotNull(message = "※必ず入力してください。")
                                         @field:Digits(integer = 5, fraction = 3,
                                                       message = "※整数{integer}桁、小数点以下{fraction}桁の範囲で入力してください。")
                                         @field:DecimalMin(value = "0.001", message = "※{value}以上の数値を入力してください。")
                                         val quantity: Double?,
                                         @field:NotEmpty(message = "※必ず入力してください。")
                                         @field:Size(max = 10, message = "※{max}文字以内で入力してください。")
                                         val takingUnit: String,
                                         @field:NotNull(message = "※必ず入力してください。")
                                         @field:Min(value = 1, message = "※{value}以上の数値を入力してください。")
                                         @field:Max(value = 100, message = "※{value}以下の数値を入力してください。")
                                         val timesPerDay: Int?,
                                         val timingOptions: List<Timing> = emptyList(),
                                         @field:Valid
                                         val effects: List<@Valid Effect> = emptyList(),
                                         @field:Size(max = 500, message = "※{max}文字以内で入力してください。")
                                         val precautions: String) {
    val validatedName: String = name.trim()
    val validatedDosage: Dosage = Dosage(quantity ?: 0.0, takingUnit.trim())
    val validatedAdministration: Administration = Administration(timesPerDay ?: 0, timingOptions)
    val validatedEffects: Effects = Effects(effects.map(Effect::value))
    val validatedPrecautions: Note = Note(precautions.trim())

    companion object {
        fun initialize(): MedicineBasicInfoInputCommand {
            return MedicineBasicInfoInputCommand("",
                                                 0.0,
                                                 "",
                                                 0,
                                                 emptyList(),
                                                 emptyList(),
                                                 "")
        }

        fun initialize(medicine: Medicine): MedicineBasicInfoInputCommand {
            return MedicineBasicInfoInputCommand(medicine.name,
                                                 medicine.dosage.quantity,
                                                 medicine.dosage.takingUnit,
                                                 medicine.administration.timesPerDay,
                                                 medicine.administration.timingOptions,
                                                 medicine.effects.values.map(::Effect),
                                                 medicine.precautions.value)
        }
    }

    fun isTimingSelected(timing: String): Boolean {
        return timingOptions.contains(Timing.valueOf(timing))
    }

    // List のバリデーションを行うために必要
    data class Effect(@field:Size(max = 30,
                                  message = "※{max}文字以内で入力してください。") val value: String) {
        override fun toString(): String = value
    }
}