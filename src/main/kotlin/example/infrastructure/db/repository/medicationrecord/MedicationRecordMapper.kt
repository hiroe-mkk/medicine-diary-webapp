package example.infrastructure.db.repository.medicationrecord

import example.domain.model.medicationrecord.*
import org.apache.ibatis.annotations.*

@Mapper
interface MedicationRecordMapper {
    fun findOneByMedicationRecordId(medicationRecordId: String): MedicationRecord?

    fun findAllByMedicineId(medicineId: String): Set<MedicationRecord>

    fun findAllByMedicineIdAndAccountId(medicineId: String, accountId: String): Set<MedicationRecord>

    fun upsert(medicationRecords: example.infrastructure.db.repository.medicationrecord.MedicationRecordSaveEntity)

    fun upsertAll(medicationRecords: Collection<example.infrastructure.db.repository.medicationrecord.MedicationRecordSaveEntity>)

    fun deleteOneByMedicationRecordId(medicationRecordId: String)

    fun deleteAllByMedicineId(medicineId: String)

    fun deleteAllByAccountId(accountId: String)
}