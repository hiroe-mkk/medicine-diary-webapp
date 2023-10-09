package example.domain.model.takingrecord

interface TakingRecordRepository {
    fun findById(takingRecordId: TakingRecordId): TakingRecord?

    fun save(takingRecord: TakingRecord)

    fun delete(takingRecordId: TakingRecordId)
}