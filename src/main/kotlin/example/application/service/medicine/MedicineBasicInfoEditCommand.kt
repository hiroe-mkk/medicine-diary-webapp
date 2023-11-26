package example.application.service.medicine

import example.domain.model.medicine.*
import example.domain.shared.type.*
import example.domain.shared.validation.*
import jakarta.validation.*
import jakarta.validation.constraints.*

/**
 * 薬基本情報の登録と更新に利用される Command クラス
 */
data class MedicineBasicInfoEditCommand(@field:NotWhitespaceOnly(message = "※お薬名を入力してください。")
                                        @field:Size(max = 30, message = "※{max}文字以内で入力してください。")
                                        val medicineName: String,
                                        @field:NotNull(message = "※1回あたりの服用量を入力してください。")
                                        @field:MedicineQuantity
                                        val quantity: Double?,
                                        @field:NotWhitespaceOnly(message = "※お薬の単位を入力してください。")
                                        @field:Size(max = 10, message = "※{max}文字以内で入力してください。")
                                        val doseUnit: String,
                                        @field:NotNull(message = "※1日当たりの服用回数を入力してください。")
                                        @field:Min(value = 1, message = "※{value}以上の数値を入力してください。")
                                        @field:Max(value = 100, message = "※{value}以下の数値を入力してください。")
                                        val timesPerDay: Int?,
                                        val timingOptions: List<Timing> = emptyList(),
                                        @field:Valid
                                        val effects: List<@Valid EffectInputField> = emptyList(),
                                        @field:Size(max = 500, message = "※{max}文字以内で入力してください。")
                                        val precautions: String,
                                        val isOwnedBySharedGroup: Boolean = false,
                                        val isPublic: Boolean = false) {
    val validatedMedicineName: MedicineName = MedicineName(medicineName.trim())
    val validatedDosageAndAdministration: DosageAndAdministration =
            DosageAndAdministration(Dose(quantity ?: 0.0),
                                    "錠",
                                    timesPerDay ?: 0,
                                    timingOptions)
    val validatedEffects: Effects = Effects(effects.map(EffectInputField::value))
    val validatedPrecautions: Note = Note(precautions.trim())

    companion object {
        fun initialize(): MedicineBasicInfoEditCommand {
            return MedicineBasicInfoEditCommand("",
                                                0.0,
                                                "",
                                                0,
                                                emptyList(),
                                                emptyList(),
                                                "",
                                                false)
        }

        fun initialize(medicine: Medicine): MedicineBasicInfoEditCommand {
            return MedicineBasicInfoEditCommand(medicine.medicineName.value,
                                                medicine.dosageAndAdministration.dose.quantity,
                                                medicine.dosageAndAdministration.doseUnit,
                                                medicine.dosageAndAdministration.timesPerDay,
                                                medicine.dosageAndAdministration.timingOptions,
                                                medicine.effects.values.map(::EffectInputField),
                                                medicine.precautions.value,
                                                medicine.isPublic)
        }
    }

    fun isTimingSelected(timing: String): Boolean {
        return timingOptions.contains(Timing.valueOf(timing))
    }

    // List のバリデーションを行うために必要
    data class EffectInputField(@field:Size(max = 30,
                                            message = "※{max}文字以内で入力してください。")
                                val value: String) {
        override fun toString(): String = value
    }
}