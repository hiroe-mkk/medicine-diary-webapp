package example.infrastructure.repository.takingrecord

import example.domain.model.takingrecord.*
import example.infrastructure.repository.shared.*
import org.apache.ibatis.annotations.*
import java.time.*

@Mapper
interface TakingRecordMapper {
    fun findOneByTakingRecordId(takingRecordId: String): TakingRecordResultEntity?

    fun countByTakenMedicine(takenMedicine: String): Long

    fun findAllByMedicineId(takenMedicine: String, pageSize: Int, offset: Long): List<TakingRecordResultEntity>

    fun upsertOneTakingRecord(takingRecordId: String,
                              recorder: String,
                              takenMedicine: String,
                              quantity: Double,
                              note: String,
                              takenAt: LocalDateTime)

    fun insertAllSymptoms(takingRecordId: String, symptoms: Collection<OrderedEntity<FollowUp>>)

    fun deleteAllSymptoms(takingRecordId: String)

    fun deleteOneTakingRecord(takingRecordId: String)
}