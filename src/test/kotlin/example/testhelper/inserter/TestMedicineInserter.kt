package example.testhelper.inserter

import example.domain.model.medicine.*
import example.domain.model.medicine.medicineImage.*
import example.domain.shared.type.*
import org.springframework.boot.test.context.*
import java.time.*

@TestComponent
class TestMedicineInserter(private val medicineRepository: MedicineRepository) {
    private var num: Int = 1

    /**
     * テスト用の薬を生成して、リポジトリに保存する
     */
    fun insert(owner: MedicineOwner,
               medicineId: MedicineId = MedicineId("testMedicineId${num++}"),
               medicineName: MedicineName = MedicineName("ロキソニンS"),
               dosageAndAdministration: DosageAndAdministration = DosageAndAdministration(Dose(1.0),
                                                                                          "錠",
                                                                                          3,
                                                                                          emptyList()),
               effects: Effects = Effects(listOf("頭痛", "解熱")),
               precautions: Note = Note("服用間隔は4時間以上開けること。"),
               medicineImageURL: MedicineImageURL? = null,
               isPublic: Boolean = false,
               inventory: Inventory? = null,
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