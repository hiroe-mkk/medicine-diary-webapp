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
                          private val takingRecordDomainService: TakingRecordDomainService,
                          private val medicineDomainService: MedicineDomainService) {
    /**
     * 修正用の服用記録編集コマンドを取得する
     */
    fun getRegistrationTakingRecordEditCommand(medicineId: MedicineId?,
                                               userSession: UserSession): TakingRecordEditCommand? {
        val availableMedicineIds = medicineDomainService.findAllAvailableMedicines(userSession.accountId).map { it.id }
        if (availableMedicineIds.isEmpty()) return null

        return if (medicineId != null && availableMedicineIds.contains(medicineId)) {
            TakingRecordEditCommand.initialize(medicineId)
        } else {
            TakingRecordEditCommand.initialize()
        }
    }

    /**
     * 服用記録を追加する
     */
    fun addTakingRecord(command: TakingRecordEditCommand, userSession: UserSession): TakingRecordId {
        val medicine = findAvailableMedicineOrElseThrowException(command.validatedTakenMedicine, userSession)
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
     * 修正用の服用記録編集コマンドを取得する
     */
    fun getModificationTakingRecordEditCommand(takingRecordId: TakingRecordId,
                                               userSession: UserSession): TakingRecordEditCommand {
        val takingRecord = findOwnedTakingRecordOrElseThrowException(takingRecordId, userSession)
        return TakingRecordEditCommand.initialize(takingRecord)
    }

    /**
     * 服用記録を修正する
     */
    fun modifyTakingRecord(takingRecordId: TakingRecordId,
                           command: TakingRecordEditCommand,
                           userSession: UserSession) {
        val medicine = findAvailableMedicineOrElseThrowException(command.validatedTakenMedicine, userSession)
        val takingRecord = findOwnedTakingRecordOrElseThrowException(takingRecordId, userSession)
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
        val takingRecord = findOwnedTakingRecordOrElseThrowException(takingRecordId, userSession)
        takingRecordRepository.delete(takingRecord.id)
    }

    private fun findOwnedTakingRecordOrElseThrowException(takingRecordId: TakingRecordId,
                                                          userSession: UserSession): TakingRecord {
        return takingRecordDomainService.findOwnedTakingRecord(takingRecordId, userSession.accountId)
               ?: throw TakingRecordNotFoundException(takingRecordId)
    }

    private fun findAvailableMedicineOrElseThrowException(medicineId: MedicineId,
                                                          userSession: UserSession): Medicine {
        return medicineDomainService.findAvailableMedicine(medicineId, userSession.accountId)
               ?: throw MedicineNotFoundException(medicineId)
    }
}