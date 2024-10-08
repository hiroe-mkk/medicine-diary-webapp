package example.domain.model.medicationrecord

import example.domain.model.account.*
import org.springframework.stereotype.*

@Component
class MedicationRecordFinder(private val medicationRecordRepository: MedicationRecordRepository) {
    fun findRecordedMedicationRecord(medicationRecordId: MedicationRecordId, accountId: AccountId): MedicationRecord? {
        val medicationRecord = medicationRecordRepository.findById(medicationRecordId) ?: return null
        return if (medicationRecord.isRecordedBy(accountId)) medicationRecord else null
    }
}
