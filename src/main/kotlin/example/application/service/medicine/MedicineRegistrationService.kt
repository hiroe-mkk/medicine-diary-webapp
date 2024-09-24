package example.application.service.medicine

import example.application.shared.usersession.*
import example.domain.model.medicine.*
import example.domain.shared.type.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class MedicineRegistrationService(private val medicineRepository: MedicineRepository,
                                  private val localDateTimeProvider: LocalDateTimeProvider,
                                  private val medicineOwnerFactory: MedicineOwnerFactory) {
    /**
     * 登録用の薬基本情報編集コマンドを取得する
     */
    @Transactional(readOnly = true)
    fun getRegistrationMedicineBasicInfoEditCommand(userSession: UserSession): MedicineBasicInfoEditCommand {
        return MedicineBasicInfoEditCommand.initialize()
    }

    /**
     * 薬を登録する
     */
    fun registerMedicine(command: MedicineBasicInfoEditCommand,
                         userSession: UserSession): MedicineId {
        val medicineOwner = medicineOwnerFactory.create(userSession.accountId, command.isOwnedBySharedGroup)
        val medicine = Medicine.create(medicineRepository.createMedicineId(),
                                       medicineOwner,
                                       command.validatedMedicineName,
                                       command.validatedDosageAndAdministration,
                                       command.validatedEffects,
                                       command.validatedPrecautions,
                                       command.isPublic,
                                       localDateTimeProvider.now())

        medicineRepository.save(medicine)
        return medicine.id
    }
}
