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
class MedicationRecordAdditionService(private val medicationRecordRepository: MedicationRecordRepository,
                                      private val medicineRepository: MedicineRepository,
                                      private val medicineFinder: MedicineFinder) {
    /**
     * 追加用の服用記録編集コマンドを取得する
     */
    @Transactional(readOnly = true)
    fun getAdditionMedicationRecordEditCommand(medicineId: MedicineId?,
                                               date: LocalDate?,
                                               userSession: UserSession): MedicationRecordEditCommand? {
        val availableMedicineIds = medicineFinder.findAllAvailableMedicines(userSession.accountId).map { it.id }
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
        val medicine = medicineFinder.findAvailableMedicine(command.validatedTakenMedicine, userSession.accountId)
                       ?: throw MedicineNotFoundException(command.validatedTakenMedicine)

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
}
