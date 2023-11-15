package example.domain.model.takingrecord

import example.domain.model.account.*
import example.domain.model.medicine.*

interface TakingRecordRepository {
    fun createTakingRecordId(): TakingRecordId

    fun findById(takingRecordId: TakingRecordId): TakingRecord?

    fun save(takingRecord: TakingRecord)

    fun deleteById(takingRecordId: TakingRecordId)

    fun deleteAllByTakenMedicine(medicineId: MedicineId)

    fun deleteByRecorder(accountId: AccountId)
}