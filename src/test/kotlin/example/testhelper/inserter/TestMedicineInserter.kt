package example.testhelper.inserter

import example.domain.model.medicine.*
import example.domain.model.medicine.medicineimage.*
import org.springframework.boot.test.context.*
import java.time.*

@TestComponent
class TestMedicineInserter(private val medicineRepository: MedicineRepository) {
    /**
     * テスト用の薬を生成して、リポジトリに保存する
     */
    fun insert(owner: MedicineOwner,
               medicineId: MedicineId = medicineRepository.createMedicineId(),
               medicineName: MedicineName = MedicineName("ロキソニンS"),
               dosageAndAdministration: DosageAndAdministration = DosageAndAdministration(Dose(1.0),
                                                                                          "錠",
                                                                                          3,
                                                                                          emptyList()),
               effects: Effects = Effects(listOf("頭痛", "解熱")),
               precautions: String = "服用間隔は4時間以上開けること。",
               medicineImageURL: MedicineImageURL? = null,
               isPublic: Boolean = false,
               inventory: Inventory? = Inventory(5.0, 12.0, null, null, 2),
               registeredAt: LocalDateTime = LocalDateTime.of(2020, 1, 1, 0, 0)): Medicine {
        val medicine = Medicine(medicineId,
                                owner,
                                medicineName,
                                dosageAndAdministration,
                                effects,
                                precautions,
                                medicineImageURL,
                                isPublic,
                                inventory,
                                registeredAt)
        medicineRepository.save(medicine)
        return medicine
    }
}