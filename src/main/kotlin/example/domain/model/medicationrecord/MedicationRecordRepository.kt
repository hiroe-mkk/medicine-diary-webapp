package example.domain.model.medicationrecord

import example.domain.model.account.*
import example.domain.model.medicine.*

interface MedicationRecordRepository {
    fun createMedicationRecordId(): MedicationRecordId

    fun isValidMedicationRecordId(medicationRecordId: MedicationRecordId): Boolean

    fun findById(medicationRecordId: MedicationRecordId): MedicationRecord?

    fun findByTakenMedicine(medicineId: MedicineId): Set<MedicationRecord>

    fun findByTakenMedicineAndRecorder(medicineId: MedicineId, recorder: AccountId): Set<MedicationRecord>

    fun save(medicationRecord: MedicationRecord)

    fun saveAll(medicationRecords: Collection<MedicationRecord>)

    fun deleteById(medicationRecordId: MedicationRecordId)

    fun deleteAllByTakenMedicine(medicineId: MedicineId)

    fun deleteByRecorder(accountId: AccountId)
}