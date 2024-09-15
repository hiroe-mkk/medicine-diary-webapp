package example.application.service.medicationrecord

import example.application.service.medicine.*
import example.application.shared.usersession.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class MedicationRecordModificationService(private val medicationRecordRepository: MedicationRecordRepository,
                                          private val medicationRecordFinder: MedicationRecordFinder,
                                          private val medicineFinder: MedicineFinder) {
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
        medicineFinder.findAvailableMedicine(command.validatedTakenMedicine, userSession.accountId)
        ?: throw MedicineNotFoundException(command.validatedTakenMedicine)

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

    private fun findRecordedMedicationRecordOrElseThrowException(medicationRecordId: MedicationRecordId,
                                                                 userSession: UserSession): MedicationRecord {
        return medicationRecordFinder.findRecordedMedicationRecord(medicationRecordId, userSession.accountId)
               ?: throw MedicationRecordNotFoundException(medicationRecordId)
    }
}
