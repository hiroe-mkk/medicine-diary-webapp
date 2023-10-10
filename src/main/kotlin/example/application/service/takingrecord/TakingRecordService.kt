package example.application.service.takingrecord

import example.application.service.medicine.*
import example.application.shared.usersession.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import example.domain.shared.type.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class TakingRecordService(private val takingRecordRepository: TakingRecordRepository,
                          private val medicineRepository: MedicineRepository,
                          private val takingRecordDetailDtoFactory: TakingRecordDetailDtoFactory,
                          private val localDateTimeProvider: LocalDateTimeProvider) {
    /**
     * 服用記録を取得する
     */
    @Transactional(readOnly = true)
    fun findTakingRecordDetail(takingRecordId: TakingRecordId, userSession: UserSession): TakingRecordDetailDto {
        val takingRecord = findTakingRecordOrElseThrowException(takingRecordId, userSession)
        return takingRecordDetailDtoFactory.create(takingRecord)
    }

    /**
     * 服用記録を追加する
     */
    fun addTakingRecord(command: TakingRecordEditCommand, userSession: UserSession): TakingRecordId {
        val medicine = findMedicineOrElseThrowException(command.validatedMedicineId)
        val takingRecord = TakingRecord.create(takingRecordRepository.createTakingRecordId(),
                                               userSession.accountId,
                                               medicine,
                                               command.validatedDose,
                                               command.validSymptoms,
                                               command.validatedNote,
                                               localDateTimeProvider.now())
        takingRecordRepository.save(takingRecord)
        return takingRecord.id
    }

    /**
     * 服用記録を修正する
     */
    fun modifyTakingRecord(takingRecordId: TakingRecordId,
                           command: TakingRecordEditCommand,
                           userSession: UserSession) {
        val medicine = findMedicineOrElseThrowException(command.validatedMedicineId)
        val takingRecord = findTakingRecordOrElseThrowException(takingRecordId, userSession)
        takingRecord.modify(medicine,
                            command.validatedDose,
                            command.validSymptoms,
                            command.validatedNote)
        takingRecordRepository.save(takingRecord)
    }

    private fun findTakingRecordOrElseThrowException(takingRecordId: TakingRecordId,
                                                     userSession: UserSession): TakingRecord {
        return findTakingRecordBy(takingRecordId, userSession) ?: throw TakingRecordNotFoundException(takingRecordId)
    }

    private fun findTakingRecordBy(takingRecordId: TakingRecordId,
                                   userSession: UserSession): TakingRecord? {
        val takingRecord = takingRecordRepository.findById(takingRecordId) ?: return null
        return if (takingRecord.isRecordedBy(userSession.accountId)) takingRecord else null
    }

    private fun findMedicineOrElseThrowException(medicineId: MedicineId): Medicine {
        return (medicineRepository.findById(medicineId) ?: throw MedicineNotFoundException(medicineId))
    }
}