package example.infrastructure.repository.takingrecord

import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import org.springframework.data.domain.*
import org.springframework.stereotype.*
import java.util.*

@Component
class MyBatisTakingRecordRepository(private val takingRecordMapper: TakingRecordMapper) : TakingRecordRepository {
    override fun createTakingRecordId(): TakingRecordId {
        return TakingRecordId(UUID.randomUUID().toString())
    }

    override fun findById(takingRecordId: TakingRecordId): TakingRecord? {
        return takingRecordMapper.findOneByTakingRecordId(takingRecordId.value)
    }

    override fun findByTakenMedicine(takenMedicine: MedicineId, pageable: Pageable): Page<TakingRecord> {
        val total = takingRecordMapper.countByTakenMedicine(takenMedicine.value)
        val content = takingRecordMapper.findAllByMedicineId(takenMedicine.value,
                                                             pageable.pageSize,
                                                             pageable.offset)
        return PageImpl(content, pageable, total)
    }

    override fun save(takingRecord: TakingRecord) {
        takingRecordMapper.upsertTakingRecord(takingRecord.id.value,
                                              takingRecord.recorder.value,
                                              takingRecord.takenMedicine.value,
                                              takingRecord.dose.quantity,
                                              takingRecord.followUp.symptom,
                                              takingRecord.followUp.beforeTaking,
                                              takingRecord.followUp.afterTaking,
                                              takingRecord.note.value,
                                              takingRecord.takenAt)
    }

    override fun delete(takingRecordId: TakingRecordId) {
        takingRecordMapper.deleteTakingRecord(takingRecordId.value)
    }
}