package example.infrastructure.repository.takingrecord

import example.domain.model.takingrecord.*
import example.infrastructure.repository.shared.*
import org.springframework.stereotype.*

@Component
class MyBatisTakingRecordRepository(private val takingRecordMapper: TakingRecordMapper) : TakingRecordRepository {
    override fun findById(takingRecordId: TakingRecordId): TakingRecord? {
        return takingRecordMapper.findOneByTakingRecordId(takingRecordId.value)?.toTakingRecord()
    }

    override fun save(takingRecord: TakingRecord) {
        takingRecordMapper.insertOneTakingRecord(takingRecord.id.value,
                                                 takingRecord.accountId.value,
                                                 takingRecord.medicineId.value,
                                                 takingRecord.dose.quantity,
                                                 takingRecord.note.value,
                                                 takingRecord.takenAt)
        insertAllSymptoms(takingRecord)
    }

    private fun insertAllSymptoms(takingRecord: TakingRecord) {
        val symptoms = takingRecord.symptoms.values
        if (symptoms.isEmpty()) return

        takingRecordMapper.insertAllSymptoms(takingRecord.id.value,
                                             OrderedEntitiesConverter.convert(symptoms))
    }
}