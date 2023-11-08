package example.application.service.medicine

import example.application.shared.usersession.*
import example.domain.model.medicine.*
import example.domain.shared.type.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class MedicineService(private val medicineRepository: MedicineRepository,
                      private val localDateTimeProvider: LocalDateTimeProvider,
                      private val medicineDomainService: MedicineDomainService) {
    /**
     * 薬詳細を取得する
     */
    @Transactional(readOnly = true)
    fun findMedicineDetail(medicineId: MedicineId, userSession: UserSession): MedicineDetailDto {
        val medicine = findUserMedicineOrElseThrowException(medicineId, userSession) // TODO: 閲覧可能な薬
        return MedicineDetailDto.from(medicine)
    }

    /**
     * 薬概要一覧を取得する
     */
    @Transactional(readOnly = true)
    fun findAllMedicineOverviews(userSession: UserSession): List<MedicineOverviewDto> {
        val medicines = medicineRepository.findByAccountId(userSession.accountId) // TODO
        return medicines.map { MedicineOverviewDto.from(it) }
    }

    /**
     * 薬を登録する
     */
    fun registerMedicine(command: MedicineBasicInfoEditCommand,
                         isWantToOwn: Boolean,
                         userSession: UserSession): MedicineId {
        val medicine = medicineDomainService.createMedicine(medicineRepository.createMedicineId(),
                                                            command.validatedMedicineName,
                                                            command.validatedDosageAndAdministration,
                                                            command.validatedEffects,
                                                            command.validatedPrecautions,
                                                            command.isPublic,
                                                            localDateTimeProvider.now(),
                                                            userSession.accountId,
                                                            isWantToOwn)
        medicineRepository.save(medicine)
        return medicine.id
    }

    /**
     * 初期化された更新用の薬基本情報編集コマンドを取得する
     */
    fun getInitializedMedicineBasicInfoEditCommand(medicineId: MedicineId,
                                                   userSession: UserSession): MedicineBasicInfoEditCommand {
        val medicine = findUserMedicineOrElseThrowException(medicineId, userSession)
        return MedicineBasicInfoEditCommand.initialize(medicine)
    }

    /**
     * 薬基本情報を更新する
     */
    fun updateMedicineBasicInfo(medicineId: MedicineId,
                                command: MedicineBasicInfoEditCommand,
                                userSession: UserSession) {
        val medicine = findUserMedicineOrElseThrowException(medicineId, userSession)
        medicine.changeBasicInfo(command.validatedMedicineName,
                                 command.validatedDosageAndAdministration,
                                 command.validatedEffects,
                                 command.validatedPrecautions,
                                 command.isPublic)
        medicineRepository.save(medicine)
    }

    /**
     * 薬を削除する
     */
    fun deleteMedicine(medicineId: MedicineId, userSession: UserSession) {
        val medicine = findUserMedicineOrElseThrowException(medicineId, userSession)
        medicineRepository.delete(medicine.id)
    }

    /**
     * 所有している薬か
     */
    fun isOwned(medicineId: MedicineId, userSession: UserSession): Boolean {
        val medicine = medicineRepository.findById(medicineId) ?: return false
        return medicine.isOwnedBy(userSession.accountId)
    }

    private fun findUserMedicineOrElseThrowException(medicineId: MedicineId,
                                                     userSession: UserSession): Medicine {
        return medicineDomainService.findUserMedicine(medicineId, userSession.accountId)
               ?: throw MedicineNotFoundException(medicineId)
    }
}