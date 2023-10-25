package example.application.service.takingrecord

import example.application.service.medicine.*
import example.application.shared.usersession.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class TakingRecordService(private val takingRecordRepository: TakingRecordRepository,
                          private val medicineRepository: MedicineRepository) {
    /**
     * 服用記録を追加する
     */
    fun addTakingRecord(command: TakingRecordEditCommand, userSession: UserSession): TakingRecordId {
        val medicine = findMedicineOrElseThrowException(command.validatedTakenMedicine)
        val takingRecord = TakingRecord.create(takingRecordRepository.createTakingRecordId(),
                                               userSession.accountId,
                                               medicine,
                                               command.validatedDose,
                                               command.validFollowUp,
                                               command.validatedNote,
                                               command.validatedTakenAt)
        takingRecordRepository.save(takingRecord)
        return takingRecord.id
    }

    /**
     * 初期化された修正用の服用記録編集コマンドを取得する
     */
    fun getInitializedTakingRecordEditCommand(takingRecordId: TakingRecordId,
                                              userSession: UserSession): TakingRecordEditCommand {
        val takingRecord = findTakingRecordOrElseThrowException(takingRecordId, userSession)
        return TakingRecordEditCommand.initialize(takingRecord)
    }

    /**
     * 服用記録を修正する
     */
    fun modifyTakingRecord(takingRecordId: TakingRecordId,
                           command: TakingRecordEditCommand,
                           userSession: UserSession) {
        val medicine = findMedicineOrElseThrowException(command.validatedTakenMedicine)
        val takingRecord = findTakingRecordOrElseThrowException(takingRecordId, userSession)
        takingRecord.modify(medicine,
                            command.validatedDose,
                            command.validFollowUp,
                            command.validatedNote,
                            command.validatedTakenAt)
        takingRecordRepository.save(takingRecord)
    }

    /**
     * 服用記録を削除する
     */
    fun deleteTakingRecord(takingRecordId: TakingRecordId, userSession: UserSession) {
        val takingRecord = findTakingRecordOrElseThrowException(takingRecordId, userSession)
        takingRecordRepository.delete(takingRecord.id)
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