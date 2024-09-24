package example.application.service.medicationrecord

import example.domain.model.medicationrecord.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional(readOnly = true)
class MedicationRecordQueryService(private val medicationRecordRepository: MedicationRecordRepository) {
    /**
     * 有効な服用記録 ID か
     */
    fun isValidMedicationRecordId(medicationRecordId: MedicationRecordId): Boolean {
        return medicationRecordRepository.isValidMedicationRecordId(medicationRecordId)
    }
}
