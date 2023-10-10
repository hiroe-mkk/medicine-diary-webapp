package example.infrastructure.repository.takingrecord

import example.domain.model.account.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import example.domain.shared.type.*
import example.infrastructure.repository.shared.*
import java.time.*

class TakingRecordResultEntity(val takingRecordId: TakingRecordId,
                               val recorder: AccountId,
                               val medicineId: MedicineId,
                               val dose: Dose,
                               val note: Note,
                               val takenAt: LocalDateTime) {
    // MyBatis ではコレクションをコンストラクタに渡すことができない
    val symptoms: List<OrderedEntity<FollowUp>> = mutableListOf()

    fun toTakingRecord(): TakingRecord {
        return TakingRecord.reconstruct(takingRecordId,
                                        recorder,
                                        medicineId,
                                        dose,
                                        Symptoms(OrderedEntitiesConverter.restore(symptoms)),
                                        note,
                                        takenAt)
    }
}