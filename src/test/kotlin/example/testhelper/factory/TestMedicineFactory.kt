package example.testhelper.factory

import example.application.service.medicine.*
import example.domain.model.account.*
import example.domain.model.medicine.*
import java.time.*

object TestMedicineFactory {
    private val defaultName = "ロキソニンS"
    private val defaultTakingUnit = "錠"
    private val defaultQuantity = 1.0
    private val defaultTimesPerDay = 3
    private val defaultTimingOptions = listOf(Timing.AS_NEEDED)
    private val defaultEffects = listOf("頭痛", "解熱", "肩こり")
    private val defaultPrecautions = "服用間隔は4時間以上開けること。"

    fun create(medicineId: MedicineId = MedicineId("testMedicineId"),
               accountId: AccountId = AccountId("testAccountId"),
               name: String = defaultName,
               takingUnit: String = defaultTakingUnit,
               dosage: Dosage = Dosage(defaultQuantity),
               administration: Administration = Administration(defaultTimesPerDay, defaultTimingOptions),
               effects: Effects = Effects(defaultEffects),
               precautions: String = defaultPrecautions,
               registeredAt: LocalDateTime = LocalDateTime.of(2020, 1, 1, 0, 0)): Medicine {
        return Medicine(medicineId,
                        accountId,
                        name,
                        takingUnit,
                        dosage,
                        administration,
                        effects,
                        precautions,
                        registeredAt)
    }

    fun createMedicineBasicInfoInputCommand(name: String = defaultName,
                                            takingUnit: String = defaultTakingUnit,
                                            quantity: Double = defaultQuantity,
                                            timesPerDay: Int = defaultTimesPerDay,
                                            timingOptions: List<Timing> = defaultTimingOptions,
                                            effects: List<String> = defaultEffects,
                                            precautions: String = defaultPrecautions): MedicineBasicInfoInputCommand {
        return MedicineBasicInfoInputCommand(name,
                                             takingUnit,
                                             quantity,
                                             timesPerDay,
                                             timingOptions,
                                             effects.map(MedicineBasicInfoInputCommand::Effect),
                                             precautions)
    }
}