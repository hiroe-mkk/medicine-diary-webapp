package example.infrastructure.repository.medicationrecord

import example.domain.model.account.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import org.springframework.stereotype.*
import java.util.*

@Component
class MyBatisMedicationRecordRepository(private val medicationRecordMapper: MedicationRecordMapper) :
        MedicationRecordRepository {
    override fun createMedicationRecordId(): MedicationRecordId {
        return MedicationRecordId(UUID.randomUUID().toString())
    }

    override fun findById(medicationRecordId: MedicationRecordId): MedicationRecord? {
        return medicationRecordMapper.findOneByMedicationRecordId(medicationRecordId.value)
    }

    override fun findByTakenMedicine(takenMedicine: MedicineId): Set<MedicationRecord> {
        return medicationRecordMapper.findAllByMedicineId(takenMedicine.value)
    }

    override fun save(medicationRecord: MedicationRecord) {
        medicationRecordMapper.upsert(MedicationRecordSaveEntity(medicationRecord.id.value,
                                                                 medicationRecord.recorder.value,
                                                                 medicationRecord.takenMedicine.value,
                                                                 medicationRecord.dose.quantity,
                                                                 medicationRecord.followUp.symptom,
                                                                 medicationRecord.followUp.beforeMedication,
                                                                 medicationRecord.followUp.afterMedication,
                                                                 medicationRecord.note.value,
                                                                 medicationRecord.takenAt))
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
                                       it.note.value,
                                       it.takenAt)
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