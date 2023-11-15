package example.infrastructure.repository.takingrecord

import example.domain.model.takingrecord.*
import org.apache.ibatis.annotations.*
import java.time.*

@Mapper
interface TakingRecordMapper {
    fun findOneByTakingRecordId(takingRecordId: String): TakingRecord?

    fun upsert(takingRecordId: String,
               recorder: String,
               takenMedicine: String,
               quantity: Double,
               symptom: String,
               beforeTaking: ConditionLevel,
               afterTaking: ConditionLevel?,
               note: String,
               takenAt: LocalDateTime)

    fun deleteOneByTakingRecordId(takingRecordId: String)

    fun deleteAllByMedicineId(medicineId: String)

    fun deleteAllByAccountId(accountId: String)
}