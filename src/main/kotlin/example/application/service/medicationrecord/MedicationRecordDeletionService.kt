package example.application.service.medicationrecord

import example.application.shared.usersession.*
import example.domain.model.medicationrecord.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class MedicationRecordDeletionService(private val medicationRecordRepository: MedicationRecordRepository,
                                      private val medicationRecordFinder: MedicationRecordFinder) {
    /**
     * 服用記録を削除する
     */
    fun deleteMedicationRecord(medicationRecordId: MedicationRecordId, userSession: UserSession) {
        val medicationRecord = medicationRecordFinder.findRecordedMedicationRecord(medicationRecordId,
                                                                                   userSession.accountId)
                               ?: return
        medicationRecordRepository.deleteById(medicationRecord.id)
    }
}
