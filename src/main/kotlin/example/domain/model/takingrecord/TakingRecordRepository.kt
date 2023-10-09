package example.domain.model.takingrecord

import example.domain.model.medicine.*
import org.springframework.data.domain.*

interface TakingRecordRepository {
    fun findById(takingRecordId: TakingRecordId): TakingRecord?

    fun findByMedicineId(medicineId: MedicineId, pageable: Pageable): Page<TakingRecord>

    fun save(takingRecord: TakingRecord)

    fun delete(takingRecordId: TakingRecordId)
}