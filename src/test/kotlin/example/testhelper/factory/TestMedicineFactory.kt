package example.testhelper.factory

import example.application.service.medicine.*
import example.application.service.medicine.MedicineBasicInfoEditCommand.*
import example.domain.model.account.*
import example.domain.model.medicine.*
import example.domain.model.medicine.medicineImage.*
import example.domain.shared.type.*
import java.time.*

object TestMedicineFactory {
    fun createMedicine(medicineId: MedicineId = MedicineId("medicineId"),
                       owner: MedicineOwner = MedicineOwner.create(AccountId("testAccountId")),
                       medicineName: MedicineName = MedicineName("ロキソニンS"),
                       dosageAndAdministration: DosageAndAdministration = DosageAndAdministration(Dose(1.0),
                                                                                                  "錠",
                                                                                                  3,
                                                                                                  emptyList()),
                       effects: Effects = Effects(listOf("頭痛", "解熱")),
                       precautions: Note = Note("服用間隔は4時間以上開けること。"),
                       medicineImageURL: MedicineImageURL? = null,
                       isPublic: Boolean = true,
                       registeredAt: LocalDateTime = LocalDateTime.of(2020, 1, 1, 0, 0)): Medicine {
        return Medicine(medicineId,
                        owner,
                        medicineName,
                        dosageAndAdministration,
                        effects,
                        precautions,
                        medicineImageURL,
                        isPublic,
                        registeredAt)
    }

    fun createCompletedRegistrationCommand(medicineName: String = "ロキソニンS",
                                           quantity: Double = 1.0,
                                           takingUnit: String = "錠",
                                           timesPerDay: Int = 3,
                                           timingOptions: List<Timing> = emptyList(),
                                           effects: List<EffectInputField> = listOf(
                                                   EffectInputField("頭痛"),
                                                   EffectInputField("解熱")),
                                           precautions: String = "服用間隔は4時間以上開けること。",
                                           isPublic: Boolean = true): MedicineBasicInfoEditCommand {
        return MedicineBasicInfoEditCommand(medicineName,
                                            quantity,
                                            takingUnit,
                                            timesPerDay,
                                            timingOptions,
                                            effects,
                                            precautions,
                                            isPublic)
    }

    fun createCompletedUpdateCommand(medicineName: String = "ロキソニンSプレミアム",
                                     quantity: Double = 2.0,
                                     takingUnit: String = "錠",
                                     timesPerDay: Int = 2,
                                     timingOptions: List<Timing> = listOf(Timing.AS_NEEDED),
                                     effects: List<EffectInputField> = listOf(EffectInputField("頭痛"),
                                                                              EffectInputField("解熱"),
                                                                              EffectInputField("肩こり")),
                                     precautions: String = "服用間隔は4時間以上開けること。\n再度症状があらわれた場合には3回目を服用してもよい。",
                                     isPublic: Boolean = true): MedicineBasicInfoEditCommand {
        return MedicineBasicInfoEditCommand(medicineName,
                                            quantity,
                                            takingUnit,
                                            timesPerDay,
                                            timingOptions,
                                            effects,
                                            precautions,
                                            isPublic)
    }
}