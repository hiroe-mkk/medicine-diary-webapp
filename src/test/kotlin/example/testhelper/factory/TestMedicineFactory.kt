package example.testhelper.factory

import example.application.service.medicine.*
import example.application.service.medicine.MedicineBasicInfoEditCommand.*
import example.domain.model.medicine.*

object TestMedicineFactory {
    private val defaultName = "ロキソニンS"
    private val defaultTakingUnit = "錠"
    private val defaultQuantity = 1.0
    private val defaultTimesPerDay = 3
    private val defaultTimingOptions = listOf(Timing.AS_NEEDED)
    private val defaultEffects = listOf("頭痛", "解熱", "肩こり")
    private val defaultPrecautions = "服用間隔は4時間以上開けること。"

    fun createMedicineBasicInfoEditCommand(name: String = defaultName,
                                           quantity: Double = defaultQuantity,
                                           takingUnit: String = defaultTakingUnit,
                                           timesPerDay: Int = defaultTimesPerDay,
                                           timingOptions: List<Timing> = defaultTimingOptions,
                                           effects: List<EffectInputField> = defaultEffects.map(
                                                   MedicineBasicInfoEditCommand::EffectInputField),
                                           precautions: String = defaultPrecautions): MedicineBasicInfoEditCommand {
        return MedicineBasicInfoEditCommand(name,
                                            quantity,
                                            takingUnit,
                                            timesPerDay,
                                            timingOptions,
                                            effects,
                                            precautions)
    }

    fun createMedicineBasicInfoEditCommand(medicine: Medicine): MedicineBasicInfoEditCommand {
        return MedicineBasicInfoEditCommand("${medicine.name}[CHANGED]",
                                            medicine.dosageAndAdministration.dose.quantity + 1,
                                            "${medicine.dosageAndAdministration.takingUnit}[CHANGED]",
                                            medicine.dosageAndAdministration.timesPerDay + 1,
                                            emptyList(),
                                            emptyList(),
                                            "${medicine.precautions}[CHANGED]")
    }
}