package example.application.service.medicine

import example.application.shared.usersession.*
import example.domain.model.medicine.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class MedicineUpdateService(private val medicineRepository: MedicineRepository,
                            private val medicineFinder: MedicineFinder,
                            private val medicineBasicInfoUpdateService: MedicineBasicInfoUpdateService) {
    /**
     * 更新用の薬基本情報編集コマンドを取得する
     */
    @Transactional(readOnly = true)
    fun getUpdateMedicineBasicInfoEditCommand(medicineId: MedicineId,
                                              userSession: UserSession): MedicineBasicInfoEditCommand {
        val medicine = findAvailableMedicineOrElseThrowException(medicineId, userSession)
        return MedicineBasicInfoEditCommand.initialize(medicine)
    }

    /**
     * 薬基本情報を更新する
     */
    fun updateMedicineBasicInfo(medicineId: MedicineId,
                                command: MedicineBasicInfoEditCommand,
                                userSession: UserSession) {
        medicineBasicInfoUpdateService.update(medicineId,
                                              command.validatedMedicineName,
                                              command.validatedDosageAndAdministration,
                                              command.validatedEffects,
                                              command.validatedPrecautions,
                                              command.isOwnedBySharedGroup,
                                              command.isPublic,
                                              userSession.accountId)
    }

    /**
     * 在庫を修正する
     */
    fun adjustInventory(medicineId: MedicineId,
                        command: InventoryAdjustmentCommand,
                        userSession: UserSession) {
        val medicine = findAvailableMedicineOrElseThrowException(medicineId, userSession)
        medicine.adjustInventory(command.validatedInventory)
        medicineRepository.save(medicine)
    }

    /**
     * 在庫管理を終了する
     */
    fun stopInventoryManagement(medicineId: MedicineId, userSession: UserSession) {
        val medicine = medicineFinder.findAvailableMedicine(medicineId, userSession.accountId) ?: return
        medicine.stopInventoryManagement()
        medicineRepository.save(medicine)
    }

    private fun findAvailableMedicineOrElseThrowException(medicineId: MedicineId,
                                                          userSession: UserSession): Medicine {
        return medicineFinder.findAvailableMedicine(medicineId, userSession.accountId)
               ?: throw MedicineNotFoundException(medicineId)
    }
}
