package example.testhelper.inserter

import example.domain.model.account.*
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
    fun insert(owner: AccountId,
               medicineId: MedicineId = MedicineId("testMedicineId${num++}"),
               name: String = "ロキソニンS",
               dosageAndAdministration: DosageAndAdministration = DosageAndAdministration(Dose(1.0),
                                                                                          "錠",
                                                                                          3,
                                                                                          emptyList()),
               effects: Effects = Effects(listOf("頭痛", "解熱")),
               precautions: Note = Note("服用間隔は4時間以上開けること。"),
               medicineImageURL: MedicineImageURL? = null,
               registeredAt: LocalDateTime = LocalDateTime.of(2020, 1, 1, 0, 0)): Medicine {
        val medicine = Medicine(medicineId,
                                owner,
                                name,
                                dosageAndAdministration,
                                effects,
                                precautions,
                                medicineImageURL,
                                registeredAt)
        medicineRepository.save(medicine)
        return medicine
    }
}