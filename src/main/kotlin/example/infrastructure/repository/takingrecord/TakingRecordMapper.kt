package example.infrastructure.repository.takingrecord

import example.domain.model.takingrecord.*
import example.infrastructure.repository.shared.*
import org.apache.ibatis.annotations.*
import java.time.*

@Mapper
interface TakingRecordMapper {
    fun findOneByTakingRecordId(takingRecordId: String): TakingRecordResultEntity?

    fun insertOneTakingRecord(takingRecordId: String,
                              accountId: String,
                              medicineId: String,
                              quantity: Double,
                              note: String,
                              takenAt: LocalDateTime)

    fun insertAllSymptoms(takingRecordId: String, symptoms: Collection<OrderedEntity<FollowUp>>)

    fun deleteAllSymptoms(takingRecordId: String)

    fun deleteOneTakingRecord(takingRecordId: String)
}