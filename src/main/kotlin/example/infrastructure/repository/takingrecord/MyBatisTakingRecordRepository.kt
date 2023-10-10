package example.infrastructure.repository.takingrecord

import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import example.infrastructure.repository.shared.*
import org.springframework.data.domain.*
import org.springframework.stereotype.*
import java.util.*

@Component
class MyBatisTakingRecordRepository(private val takingRecordMapper: TakingRecordMapper) : TakingRecordRepository {
    override fun createTakingRecordId(): TakingRecordId {
        return TakingRecordId(UUID.randomUUID().toString())
    }

    override fun findById(takingRecordId: TakingRecordId): TakingRecord? {
        return takingRecordMapper.findOneByTakingRecordId(takingRecordId.value)?.toTakingRecord()
    }

    override fun findByTakenMedicine(takenMedicine: MedicineId, pageable: Pageable): Page<TakingRecord> {
        val total = takingRecordMapper.countByTakenMedicine(takenMedicine.value)
        val content = takingRecordMapper.findAllByMedicineId(takenMedicine.value,
                                                             pageable.pageSize,
                                                             pageable.offset).map { it.toTakingRecord() }
        return PageImpl(content, pageable, total)
    }

    override fun save(takingRecord: TakingRecord) {
        takingRecordMapper.upsertOneTakingRecord(takingRecord.id.value,
                                                 takingRecord.recorder.value,
                                                 takingRecord.takenMedicine.value,
                                                 takingRecord.dose.quantity,
                                                 takingRecord.note.value,
                                                 takingRecord.takenAt)
        upsertAllSymptoms(takingRecord)
    }

    private fun upsertAllSymptoms(takingRecord: TakingRecord) {
        takingRecordMapper.deleteAllSymptoms(takingRecord.id.value)
        val symptoms = takingRecord.symptoms.values
        if (symptoms.isEmpty()) return

        takingRecordMapper.insertAllSymptoms(takingRecord.id.value,
                                             OrderedEntitiesConverter.convert(symptoms))
    }

    override fun delete(takingRecordId: TakingRecordId) {
        takingRecordMapper.deleteAllSymptoms(takingRecordId.value)
        takingRecordMapper.deleteOneTakingRecord(takingRecordId.value)
    }
}