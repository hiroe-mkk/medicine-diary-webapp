package example.infrastructure.db.repository.medicationrecord

import example.domain.model.account.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import example.infrastructure.db.repository.shared.*
import org.springframework.stereotype.*

@Component
class MyBatisMedicationRecordRepository(private val medicationRecordMapper: MedicationRecordMapper) :
        MedicationRecordRepository {
    override fun createMedicationRecordId(): MedicationRecordId {
        return MedicationRecordId(EntityIdHelper.generate())
    }

    override fun isValidMedicationRecordId(medicationRecordId: MedicationRecordId): Boolean {
        return EntityIdHelper.isValid(medicationRecordId)
    }

    override fun findById(medicationRecordId: MedicationRecordId): MedicationRecord? {
        return medicationRecordMapper.findOneByMedicationRecordId(medicationRecordId.value)
    }

    override fun findByTakenMedicine(medicineId: MedicineId): Set<MedicationRecord> {
        return medicationRecordMapper.findAllByMedicineId(medicineId.value)
    }

    override fun findByTakenMedicineAndRecorder(medicineId: MedicineId, recorder: AccountId): Set<MedicationRecord> {
        return medicationRecordMapper.findAllByMedicineIdAndAccountId(medicineId.value, recorder.value)
    }

    override fun save(medicationRecord: MedicationRecord) {
        medicationRecordMapper.upsert(MedicationRecordSaveEntity(
                medicationRecord.id.value,
                medicationRecord.recorder.value,
                medicationRecord.takenMedicine.value,
                medicationRecord.dose.quantity,
                medicationRecord.followUp.symptom,
                medicationRecord.followUp.beforeMedication,
                medicationRecord.followUp.afterMedication,
                medicationRecord.note,
                medicationRecord.takenMedicineOn,
                medicationRecord.takenMedicineAt,
                medicationRecord.symptomOnsetAt,
                medicationRecord.onsetEffectAt))
    }

    override fun saveAll(medicationRecords: Collection<MedicationRecord>) {
        if (medicationRecords.isEmpty()) return
        medicationRecordMapper.upsertAll(medicationRecords.map {
            MedicationRecordSaveEntity(it.id.value,
                                       it.recorder.value,
                                       it.takenMedicine.value,
                                       it.dose.quantity,
                                       it.followUp.symptom,
                                       it.followUp.beforeMedication,
                                       it.followUp.afterMedication,
                                       it.note,
                                       it.takenMedicineOn,
                                       it.takenMedicineAt,
                                       it.symptomOnsetAt,
                                       it.onsetEffectAt)
        })
    }

    override fun deleteById(medicationRecordId: MedicationRecordId) {
        medicationRecordMapper.deleteOneByMedicationRecordId(medicationRecordId.value)
    }

    override fun deleteAllByTakenMedicine(medicineId: MedicineId) {
        medicationRecordMapper.deleteAllByMedicineId(medicineId.value)
    }

    override fun deleteByRecorder(accountId: AccountId) {
        medicationRecordMapper.deleteAllByAccountId(accountId.value)
    }
}
