package example.infrastructure.repository.takingrecord

import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import example.infrastructure.repository.shared.*
import org.springframework.data.domain.*
import org.springframework.stereotype.*

@Component
class MyBatisTakingRecordRepository(private val takingRecordMapper: TakingRecordMapper) : TakingRecordRepository {
    override fun findById(takingRecordId: TakingRecordId): TakingRecord? {
        return takingRecordMapper.findOneByTakingRecordId(takingRecordId.value)?.toTakingRecord()
    }

    override fun findByMedicineId(medicineId: MedicineId, pageable: Pageable): Page<TakingRecord> {
        val total = takingRecordMapper.countByMedicineId(medicineId.value)
        val content = takingRecordMapper.findAllByMedicineId(medicineId.value,
                                                             pageable.pageSize,
                                                             pageable.offset).map { it.toTakingRecord() }
        return PageImpl(content, pageable, total)
    }

    override fun save(takingRecord: TakingRecord) {
        takingRecordMapper.upsertOneTakingRecord(takingRecord.id.value,
                                                 takingRecord.recorder.value,
                                                 takingRecord.medicineId.value,
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