package example.domain.model.medicationrecord

import example.domain.model.account.*
import example.domain.model.medicine.*

interface MedicationRecordRepository {
    fun createMedicationRecordId(): MedicationRecordId

    fun findById(medicationRecordId: MedicationRecordId): MedicationRecord?

    fun save(medicationRecord: MedicationRecord)

    fun deleteById(medicationRecordId: MedicationRecordId)

    fun deleteAllByTakenMedicine(medicineId: MedicineId)

    fun deleteByRecorder(accountId: AccountId)
}