package example.domain.model.takingrecord

import example.domain.model.medicine.*
import org.springframework.data.domain.*

interface TakingRecordRepository {
    fun createTakingRecordId(): TakingRecordId

    fun findById(takingRecordId: TakingRecordId): TakingRecord?

    fun findByTakenMedicine(takenMedicine: MedicineId, pageable: Pageable): Page<TakingRecord>

    fun save(takingRecord: TakingRecord)

    fun delete(takingRecordId: TakingRecordId)
}