package example.infrastructure.repository.medicationrecord

import example.domain.model.medicationrecord.*
import org.apache.ibatis.annotations.*
import java.time.*

@Mapper
interface MedicationRecordMapper {
    fun findOneByMedicationRecordId(medicationRecordId: String): MedicationRecord?

    fun upsert(medicationRecordId: String,
               recorder: String,
               takenMedicine: String,
               quantity: Double,
               symptom: String,
               beforeMedication: ConditionLevel,
               afterMedication: ConditionLevel?,
               note: String,
               takenAt: LocalDateTime)

    fun deleteOneByMedicationRecordId(medicationRecordId: String)

    fun deleteAllByMedicineId(medicineId: String)

    fun deleteAllByAccountId(accountId: String)
}