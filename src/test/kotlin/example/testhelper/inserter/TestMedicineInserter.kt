package example.testhelper.inserter

import example.domain.model.account.*
import example.domain.model.medicine.*
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
               takingUnit: String = "錠",
               dosage: Dosage = Dosage(1.0),
               administration: Administration = Administration(3, listOf(Timing.AS_NEEDED)),
               effects: Effects = Effects(listOf("頭痛", "解熱", "肩こり")),
               precautions: String = "服用間隔は4時間以上開けること。",
               registeredAt: LocalDateTime = LocalDateTime.of(2020, 1, 1, 0, 0)): Medicine {
        val medicine = Medicine(medicineId,
                                owner,
                                name,
                                takingUnit,
                                dosage,
                                administration,
                                effects,
                                precautions,
                                registeredAt)
        medicineRepository.save(medicine)
        return medicine
    }
}