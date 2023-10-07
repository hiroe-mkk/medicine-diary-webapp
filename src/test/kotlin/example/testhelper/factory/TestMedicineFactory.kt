package example.testhelper.factory

import example.application.service.medicine.*
import example.domain.model.account.*
import example.domain.model.medicine.*
import example.domain.model.medicine.medicineImage.*
import example.domain.shared.type.*
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
               dosage: Dosage = Dosage(defaultQuantity, defaultTakingUnit),
               administration: Administration = Administration(defaultTimesPerDay, defaultTimingOptions),
               effects: Effects = Effects(defaultEffects),
               precautions: Note = Note(defaultPrecautions),
               medicineImageURL: MedicineImageURL? = null,
               registeredAt: LocalDateTime = LocalDateTime.of(2020, 1, 1, 0, 0)): Medicine {
        return Medicine(medicineId,
                        accountId,
                        name,
                        dosage,
                        administration,
                        effects,
                        precautions,
                        medicineImageURL,
                        registeredAt)
    }

    fun createMedicineBasicInfoInputCommand(name: String = defaultName,
                                            quantity: Double = defaultQuantity,
                                            takingUnit: String = defaultTakingUnit,
                                            timesPerDay: Int = defaultTimesPerDay,
                                            timingOptions: List<Timing> = defaultTimingOptions,
                                            effects: List<String> = defaultEffects,
                                            precautions: String = defaultPrecautions): MedicineBasicInfoInputCommand {
        return MedicineBasicInfoInputCommand(name,
                                             quantity,
                                             takingUnit,
                                             timesPerDay,
                                             timingOptions,
                                             effects.map(MedicineBasicInfoInputCommand::Effect),
                                             precautions)
    }

    fun createMedicineBasicInfoInputCommand(medicine: Medicine): MedicineBasicInfoInputCommand {
        return MedicineBasicInfoInputCommand("${medicine.name}[CHANGED]",
                                             medicine.dosage.quantity + 1,
                                             "${medicine.dosage.takingUnit}[CHANGED]",
                                             medicine.administration.timesPerDay + 1,
                                             emptyList(),
                                             emptyList(),
                                             "${medicine.precautions}[CHANGED]")
    }
}