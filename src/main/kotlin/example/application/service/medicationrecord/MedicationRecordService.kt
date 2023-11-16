package example.application.service.medicationrecord

import example.application.service.medicine.*
import example.application.shared.usersession.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class MedicationRecordService(private val medicationRecordRepository: MedicationRecordRepository,
                              private val medicationRecordQueryService: MedicationRecordQueryService,
                              private val medicineQueryService: MedicineQueryService) {
    /**
     * 追加用の服用記録編集コマンドを取得する
     */
    @Transactional(readOnly = true)
    fun getAdditionMedicationRecordEditCommand(medicineId: MedicineId?,
                                               userSession: UserSession): MedicationRecordEditCommand? {
        val availableMedicineIds = medicineQueryService.findAllAvailableMedicines(userSession.accountId).map { it.id }
        if (availableMedicineIds.isEmpty()) return null

        return if (medicineId != null && availableMedicineIds.contains(medicineId)) {
            MedicationRecordEditCommand.initialize(medicineId)
        } else {
            MedicationRecordEditCommand.initialize()
        }
    }

    /**
     * 服用記録を追加する
     */
    fun addMedicationRecord(command: MedicationRecordEditCommand, userSession: UserSession): MedicationRecordId {
        val medicine = findAvailableMedicineOrElseThrowException(command.validatedTakenMedicine, userSession)
        val medicationRecord = MedicationRecord.create(medicationRecordRepository.createMedicationRecordId(),
                                                       userSession.accountId,
                                                       medicine,
                                                       command.validatedDose,
                                                       command.validFollowUp,
                                                       command.validatedNote,
                                                       command.validatedTakenAt)
        medicationRecordRepository.save(medicationRecord)
        return medicationRecord.id
    }

    /**
     * 修正用の服用記録編集コマンドを取得する
     */
    @Transactional(readOnly = true)
    fun getModificationEditCommand(medicationRecordId: MedicationRecordId,
                                   userSession: UserSession): MedicationRecordEditCommand {
        val medicationRecord = findOwnedMedicationRecordOrElseThrowException(medicationRecordId, userSession)
        return MedicationRecordEditCommand.initialize(medicationRecord)
    }

    /**
     * 服用記録を修正する
     */
    fun modifyMedicationRecord(medicationRecordId: MedicationRecordId,
                               command: MedicationRecordEditCommand,
                               userSession: UserSession) {
        val medicine = findAvailableMedicineOrElseThrowException(command.validatedTakenMedicine, userSession)
        val medicationRecord = findOwnedMedicationRecordOrElseThrowException(medicationRecordId, userSession)
        medicationRecord.modify(medicine,
                                command.validatedDose,
                                command.validFollowUp,
                                command.validatedNote,
                                command.validatedTakenAt)
        medicationRecordRepository.save(medicationRecord)
    }

    /**
     * 服用記録を削除する
     */
    fun deleteMedicationRecord(medicationRecordId: MedicationRecordId, userSession: UserSession) {
        val medicationRecord = medicationRecordQueryService.findOwnedMedicationRecord(medicationRecordId,
                                                                                      userSession.accountId) ?: return
        medicationRecordRepository.deleteById(medicationRecord.id)
    }

    private fun findOwnedMedicationRecordOrElseThrowException(medicationRecordId: MedicationRecordId,
                                                              userSession: UserSession): MedicationRecord {
        return medicationRecordQueryService.findOwnedMedicationRecord(medicationRecordId, userSession.accountId)
               ?: throw MedicationRecordNotFoundException(medicationRecordId)
    }

    private fun findAvailableMedicineOrElseThrowException(medicineId: MedicineId,
                                                          userSession: UserSession): Medicine {
        return medicineQueryService.findAvailableMedicine(medicineId, userSession.accountId)
               ?: throw MedicineNotFoundException(medicineId)
    }
}