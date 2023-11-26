package example.testhelper.factory

import example.application.service.medicine.*
import example.application.service.medicine.MedicineBasicInfoEditCommand.*
import example.domain.model.account.*
import example.domain.model.medicine.*
import example.domain.model.medicine.medicineimage.*
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
                       inventory: Inventory? = Inventory(5.0, 12.0, null, null, 2),
                       registeredAt: LocalDateTime = LocalDateTime.of(2020, 1, 1, 0, 0)): Medicine {
        return Medicine(medicineId,
                        owner,
                        medicineName,
                        dosageAndAdministration,
                        effects,
                        precautions,
                        medicineImageURL,
                        isPublic,
                        inventory,
                        registeredAt)
    }

    fun createCompletedRegistrationMedicineBasicInfoEditCommand(medicineName: String = "ロキソニンS",
                                                                quantity: Double = 1.0,
                                                                doseUnit: String = "錠",
                                                                timesPerDay: Int = 3,
                                                                timingOptions: List<Timing> = emptyList(),
                                                                effects: List<EffectInputField> = listOf(
                                                                        EffectInputField("頭痛"),
                                                                        EffectInputField("解熱")),
                                                                precautions: String = "服用間隔は4時間以上開けること。",
                                                                isOwnedBySharedGroup: Boolean = false,
                                                                isPublic: Boolean = true): MedicineBasicInfoEditCommand {
        return MedicineBasicInfoEditCommand(medicineName,
                                            quantity,
                                            doseUnit,
                                            timesPerDay,
                                            timingOptions,
                                            effects,
                                            precautions,
                                            isOwnedBySharedGroup,
                                            isPublic)
    }

    fun createCompletedUpdateMedicineBasicInfoEditCommand(medicineName: String = "ロキソニンSプレミアム",
                                                          quantity: Double = 2.0,
                                                          doseUnit: String = "錠",
                                                          timesPerDay: Int = 2,
                                                          timingOptions: List<Timing> = listOf(Timing.AS_NEEDED),
                                                          effects: List<EffectInputField> = listOf(EffectInputField("頭痛"),
                                                                                                   EffectInputField("解熱"),
                                                                                                   EffectInputField("肩こり")),
                                                          precautions: String = "服用間隔は4時間以上開けること。\n再度症状があらわれた場合には3回目を服用してもよい。",
                                                          isOwnedBySharedGroup: Boolean = false,
                                                          isPublic: Boolean = true): MedicineBasicInfoEditCommand {
        return MedicineBasicInfoEditCommand(medicineName,
                                            quantity,
                                            doseUnit,
                                            timesPerDay,
                                            timingOptions,
                                            effects,
                                            precautions,
                                            isOwnedBySharedGroup,
                                            isPublic)
    }

    fun createCompletedInventoryAdjustmentCommand(remainingQuantity: Double? = 0.0,
                                                  quantityPerPackage: Double? = 0.0,
                                                  startedOn: LocalDate? = null,
                                                  expirationOn: LocalDate? = null,
                                                  unusedPackage: Int? = 0): InventoryAdjustmentCommand {
        return InventoryAdjustmentCommand(remainingQuantity, quantityPerPackage, startedOn, expirationOn, unusedPackage)
    }
}