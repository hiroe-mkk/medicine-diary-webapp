package example.application.service.takingrecord

import example.application.shared.usersession.*
import example.domain.model.takingrecord.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class TakingRecordService(private val takenRecordRepository: TakingRecordRepository,
                          private val takingRecordDetailDtoFactory: TakingRecordDetailDtoFactory) {
    /**
     * 服用記録を取得する
     */
    @Transactional(readOnly = true)
    fun findTakingRecordDetail(takingRecordId: TakingRecordId, userSession: UserSession): TakingRecordDetailDto {
        val takingRecord = findTakingRecordOrElseThrowException(takingRecordId, userSession)
        return takingRecordDetailDtoFactory.create(takingRecord)
    }

    private fun findTakingRecordOrElseThrowException(takingRecordId: TakingRecordId,
                                                     userSession: UserSession): TakingRecord {
        return findTakingRecordBy(takingRecordId, userSession) ?: throw TakingRecordNotFoundException(takingRecordId)
    }

    private fun findTakingRecordBy(takingRecordId: TakingRecordId,
                                   userSession: UserSession): TakingRecord? {
        val takingRecord = takenRecordRepository.findById(takingRecordId) ?: return null
        return if (takingRecord.isRecordedBy(userSession.accountId)) takingRecord else null
    }
}