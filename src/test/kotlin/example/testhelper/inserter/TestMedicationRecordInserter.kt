package example.testhelper.inserter

import example.domain.model.account.*
import example.domain.model.medicine.*
import example.domain.model.medicationrecord.*
import example.domain.shared.type.*
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
               note: Note = Note(""),
               takenAt: LocalDateTime = LocalDateTime.of(2020, 1, 1, 0, 0)): MedicationRecord {
        val medicationRecord = MedicationRecord.reconstruct(medicationRecordId,
                                                            accountId,
                                                            takenMedicine,
                                                            dose,
                                                            followUp,
                                                            note,
                                                            takenAt)
        medicationRecordRepository.save(medicationRecord)
        return medicationRecord
    }
}