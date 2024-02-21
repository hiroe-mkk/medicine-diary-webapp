package example.application.service.medicationrecord

import example.application.service.medicine.*
import example.application.shared.usersession.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*
import java.time.*

@Service
@Transactional
class MedicationRecordService(private val medicationRecordRepository: MedicationRecordRepository,
                              private val medicineRepository: MedicineRepository,
                              private val medicationRecordQueryService: MedicationRecordQueryService,
                              private val medicineQueryService: MedicineQueryService) {
    /**
     * 有効な服用記録 ID か
     */
    @Transactional(readOnly = true)
    fun isValidMedicationRecordId(medicationRecordId: MedicationRecordId): Boolean {
        return medicationRecordRepository.isValidMedicationRecordId(medicationRecordId)
    }

    /**
     * 追加用の服用記録編集コマンドを取得する
     */
    @Transactional(readOnly = true)
    fun getAdditionMedicationRecordEditCommand(medicineId: MedicineId?,
                                               date: LocalDate?,
                                               userSession: UserSession): MedicationRecordEditCommand? {
        val availableMedicineIds = medicineQueryService.findAllAvailableMedicines(userSession.accountId).map { it.id }
        if (availableMedicineIds.isEmpty()) return null

        return if (medicineId != null && availableMedicineIds.contains(medicineId)) {
            MedicationRecordEditCommand.initialize(medicineId, date)
        } else {
            MedicationRecordEditCommand.initialize(date = date)
        }
    }

    /**
     * 服用記録を追加する
     */
    fun addMedicationRecord(command: MedicationRecordEditCommand, userSession: UserSession): MedicationRecordId {
        val medicine = findAvailableMedicineOrElseThrowException(command.validatedTakenMedicine, userSession)
        val medicationRecord = medicine.taken(medicationRecordRepository.createMedicationRecordId(),
                                              userSession.accountId,
                                              command.validatedDose,
                                              command.validFollowUp,
                                              command.validatedNote,
                                              command.validatedTakenMedicineOn,
                                              command.validatedTakenMedicineAt,
                                              command.validatedSymptomOnsetAt,
                                              command.validatedOnsetEffectAt)
        medicineRepository.save(medicine)
        medicationRecordRepository.save(medicationRecord)
        return medicationRecord.id
    }

    /**
     * 修正用の服用記録編集コマンドを取得する
     */
    @Transactional(readOnly = true)
    fun getModificationEditCommand(medicationRecordId: MedicationRecordId,
                                   userSession: UserSession): MedicationRecordEditCommand {
        val medicationRecord = findRecordedMedicationRecordOrElseThrowException(medicationRecordId, userSession)
        return MedicationRecordEditCommand.initialize(medicationRecord)
    }

    /**
     * 服用記録を修正する
     */
    fun modifyMedicationRecord(medicationRecordId: MedicationRecordId,
                               command: MedicationRecordEditCommand,
                               userSession: UserSession) {
        findAvailableMedicineOrElseThrowException(command.validatedTakenMedicine, userSession)
        val medicationRecord = findRecordedMedicationRecordOrElseThrowException(medicationRecordId, userSession)
        medicationRecord.changeAttributes(command.validatedTakenMedicine,
                                          command.validatedDose,
                                          command.validFollowUp,
                                          command.validatedNote,
                                          command.validatedTakenMedicineOn,
                                          command.validatedTakenMedicineAt,
                                          command.validatedSymptomOnsetAt,
                                          command.validatedOnsetEffectAt)
        medicationRecordRepository.save(medicationRecord)
    }

    /**
     * 服用記録を削除する
     */
    fun deleteMedicationRecord(medicationRecordId: MedicationRecordId, userSession: UserSession) {
        val medicationRecord = medicationRecordQueryService.findRecordedMedicationRecord(medicationRecordId,
                                                                                         userSession.accountId)
                               ?: return
        medicationRecordRepository.deleteById(medicationRecord.id)
    }

    private fun findRecordedMedicationRecordOrElseThrowException(medicationRecordId: MedicationRecordId,
                                                                 userSession: UserSession): MedicationRecord {
        return medicationRecordQueryService.findRecordedMedicationRecord(medicationRecordId, userSession.accountId)
               ?: throw MedicationRecordNotFoundException(medicationRecordId)
    }

    private fun findAvailableMedicineOrElseThrowException(medicineId: MedicineId,
                                                          userSession: UserSession): Medicine {
        return medicineQueryService.findAvailableMedicine(medicineId, userSession.accountId)
               ?: throw MedicineNotFoundException(medicineId)
    }
}