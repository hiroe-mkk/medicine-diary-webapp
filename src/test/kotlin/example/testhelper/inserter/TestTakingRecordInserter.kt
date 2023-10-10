package example.testhelper.inserter

import example.domain.model.account.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import example.domain.shared.type.*
import org.springframework.boot.test.context.*
import java.time.*

@TestComponent
class TestTakingRecordInserter(private val takingRecordRepository: TakingRecordRepository) {
    private var num: Int = 1

    /**
     * テスト用の服用記録を生成して、リポジトリに保存する
     */
    fun insert(accountId: AccountId,
               medicineId: MedicineId,
               takingRecordId: TakingRecordId = TakingRecordId("testTakingRecordId${num++}"),
               dose: Dose = Dose(1.0),
               symptoms: Symptoms = Symptoms(listOf(FollowUp("頭痛",
                                                             ConditionLevel.A_LITTLE_BAD,
                                                             ConditionLevel.GOOD))),
               note: Note = Note("それほど酷い頭痛ではなかったけれど、早めに飲んでおいたらいつもより早めに治った気がする。"),
               takenAt: LocalDateTime = LocalDateTime.of(2020, 1, 1, 0, 0)): TakingRecord {
        val takingRecord = TakingRecord.reconstruct(takingRecordId,
                                                    accountId,
                                                    medicineId,
                                                    dose,
                                                    symptoms,
                                                    note,
                                                    takenAt)
        takingRecordRepository.save(takingRecord)
        return takingRecord
    }
}