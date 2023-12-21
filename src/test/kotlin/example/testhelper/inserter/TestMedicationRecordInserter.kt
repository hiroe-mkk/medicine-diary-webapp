package example.testhelper.inserter

import example.domain.model.account.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import org.springframework.boot.test.context.*
import java.time.*

@TestComponent
class TestMedicationRecordInserter(private val medicationRecordRepository: MedicationRecordRepository) {
    private var num: Int = 1

    /**
     * テスト用の服用記録を生成して、リポジトリに保存する
     */
    fun insert(accountId: AccountId,
               takenMedicine: MedicineId,
               medicationRecordId: MedicationRecordId = MedicationRecordId("testMedicationRecordId${num++}"),
               dose: Dose = Dose(1.0),
               followUp: FollowUp = FollowUp("頭痛", ConditionLevel.A_LITTLE_BAD, null),
               note: String = "",
               takenMedicineOn: LocalDate = LocalDate.of(2020, 1, 2),
               takenMedicineAt: LocalTime = LocalTime.of(7, 0),
               symptomOnsetAt: LocalTime? = LocalTime.of(6, 30),
               onsetEffectAt: LocalTime? = null): MedicationRecord {
        val medicationRecord = MedicationRecord(medicationRecordId,
                                                accountId,
                                                takenMedicine,
                                                dose,
                                                followUp,
                                                note,
                                                takenMedicineOn,
                                                takenMedicineAt,
                                                symptomOnsetAt,
                                                onsetEffectAt)
        medicationRecordRepository.save(medicationRecord)
        return medicationRecord
    }
}