package example.testhelper.factory

import example.application.service.medicine.*
import example.application.service.medicine.MedicineBasicInfoEditCommand.*
import example.domain.model.medicine.*

object TestMedicineBasicInfoEditCommandFactory {
    fun createCompletedRegistrationCommand(name: String = "ロキソニンS",
                                           quantity: Double = 1.0,
                                           takingUnit: String = "錠",
                                           timesPerDay: Int = 3,
                                           timingOptions: List<Timing> = emptyList(),
                                           effects: List<EffectInputField> = listOf(
                                                   EffectInputField("頭痛"),
                                                   EffectInputField("解熱")),
                                           precautions: String = "服用間隔は4時間以上開けること。"): MedicineBasicInfoEditCommand {
        return MedicineBasicInfoEditCommand(name,
                                            quantity,
                                            takingUnit,
                                            timesPerDay,
                                            timingOptions,
                                            effects,
                                            precautions)
    }

    fun createCompletedUpdateCommand(name: String = "ロキソニンSプレミアム",
                                     quantity: Double = 2.0,
                                     takingUnit: String = "錠",
                                     timesPerDay: Int = 2,
                                     timingOptions: List<Timing> = listOf(Timing.AS_NEEDED),
                                     effects: List<EffectInputField> = listOf(EffectInputField("頭痛"),
                                                                              EffectInputField("解熱"),
                                                                              EffectInputField("肩こり")),
                                     precautions: String = "服用間隔は4時間以上開けること。\n再度症状があらわれた場合には3回目を服用してもよい。"): MedicineBasicInfoEditCommand {
        return MedicineBasicInfoEditCommand(name,
                                            quantity,
                                            takingUnit,
                                            timesPerDay,
                                            timingOptions,
                                            effects,
                                            precautions)
    }
}