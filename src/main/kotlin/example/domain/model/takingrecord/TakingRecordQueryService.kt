package example.domain.model.takingrecord

import example.domain.model.account.*
import org.springframework.stereotype.*

@Component
class TakingRecordQueryService(private val takingRecordRepository: TakingRecordRepository) {
    fun findOwnedTakingRecord(takingRecordId: TakingRecordId, accountId: AccountId): TakingRecord? {
        val takingRecord = takingRecordRepository.findById(takingRecordId) ?: return null
        return if (takingRecord.isRecordedBy(accountId)) takingRecord else null
    }
}