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
        val medicine = findMedicineOrElseThrowException(medicineId, userSession)
        return MedicineDetailDto.from(medicine)
    }

    /**
     * 薬概要一覧を取得する
     */
    @Transactional(readOnly = true)
    fun findAllMedicineOverviews(userSession: UserSession): List<MedicineOverviewDto> {
        val medicines = medicineRepository.findByAccountId(userSession.accountId)
        return medicines.map { MedicineOverviewDto.from(it) }
    }

    /**
     * 薬を登録する
     */
    fun registerMedicine(command: MedicineBasicInfoEditCommand, userSession: UserSession): MedicineId {
        val medicine = medicineDomainService.createMedicine(medicineRepository.createMedicineId(),
                                                            command.validatedMedicineName,
                                                            command.validatedDosageAndAdministration,
                                                            command.validatedEffects,
                                                            command.validatedPrecautions,
                                                            command.isPublic,
                                                            localDateTimeProvider.now(),
                                                            userSession.accountId)
        medicineRepository.save(medicine)
        return medicine.id
    }

    /**
     * 初期化された更新用の薬基本情報編集コマンドを取得する
     */
    fun getInitializedMedicineBasicInfoEditCommand(medicineId: MedicineId,
                                                   userSession: UserSession): MedicineBasicInfoEditCommand {
        val medicine = findMedicineOrElseThrowException(medicineId, userSession)
        return MedicineBasicInfoEditCommand.initialize(medicine)
    }

    /**
     * 薬基本情報を更新する
     */
    fun updateMedicineBasicInfo(medicineId: MedicineId,
                                command: MedicineBasicInfoEditCommand,
                                userSession: UserSession) {
        val medicine = findMedicineOrElseThrowException(medicineId, userSession)
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
        val medicine = findMedicineOrElseThrowException(medicineId, userSession)
        medicineRepository.delete(medicine.id)
    }

    private fun findMedicineOrElseThrowException(medicineId: MedicineId,
                                                 userSession: UserSession): Medicine {
        return findMedicineOwnedBy(medicineId, userSession) ?: throw MedicineNotFoundException(medicineId)
    }

    private fun findMedicineOwnedBy(medicineId: MedicineId, userSession: UserSession): Medicine? { // TODO
        val medicine = medicineRepository.findById(medicineId) ?: return null
        return if (medicine.isOwnedBy(userSession.accountId)) medicine else null
    }
}