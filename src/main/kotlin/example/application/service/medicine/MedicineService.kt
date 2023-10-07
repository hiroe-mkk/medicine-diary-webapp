package example.application.service.medicine

import example.application.shared.usersession.*
import example.domain.model.medicine.*
import example.domain.model.medicine.medicineImage.*
import example.domain.shared.type.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class MedicineService(private val medicineRepository: MedicineRepository,
                      private val medicineImageStorage: MedicineImageStorage,
                      private val localDateTimeProvider: LocalDateTimeProvider) {
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
    fun registerMedicine(medicineBasicInfoInputCommand: MedicineBasicInfoInputCommand,
                         userSession: UserSession): MedicineId {
        val medicine = Medicine.create(medicineRepository.createMedicineId(),
                                       userSession.accountId,
                                       medicineBasicInfoInputCommand.validatedName,
                                       medicineBasicInfoInputCommand.validatedDosage,
                                       medicineBasicInfoInputCommand.validatedAdministration,
                                       medicineBasicInfoInputCommand.validatedEffects,
                                       medicineBasicInfoInputCommand.validatedPrecautions,
                                       localDateTimeProvider.now())
        medicineRepository.save(medicine)
        return medicine.id
    }

    /**
     * 初期化された MedicineBasicInfoInputCommand インスタンスを取得する
     */
    fun getInitializedMedicineBasicInfoInputCommand(medicineId: MedicineId,
                                                    userSession: UserSession): MedicineBasicInfoInputCommand {
        val medicine = findMedicineOrElseThrowException(medicineId, userSession)
        return MedicineBasicInfoInputCommand.initialize(medicine)
    }

    /**
     * 薬基本情報を更新する
     */
    fun updateMedicineBasicInfo(medicineId: MedicineId,
                                medicineBasicInfoInputCommand: MedicineBasicInfoInputCommand,
                                userSession: UserSession) {
        val medicine = findMedicineOrElseThrowException(medicineId, userSession)
        medicine.changeBasicInfo(medicineBasicInfoInputCommand.validatedName,
                                 medicineBasicInfoInputCommand.validatedDosage,
                                 medicineBasicInfoInputCommand.validatedAdministration,
                                 medicineBasicInfoInputCommand.validatedEffects,
                                 medicineBasicInfoInputCommand.validatedPrecautions)
        medicineRepository.save(medicine)
    }

    /**
     * 薬画像を変更する
     */
    fun changeMedicineImage(medicineId: MedicineId,
                            command: MedicineImageChangeCommand,
                            userSession: UserSession): MedicineImageURL {
        val medicine = findMedicineOrElseThrowException(medicineId, userSession)

        medicine.medicineImageURL?.let { medicineImageStorage.delete(it) }

        val medicineImageURL = medicineImageStorage.createURL()
        medicine.changeMedicineImage(medicineImageURL)
        medicineRepository.save(medicine)

        val medicineImage = MedicineImage(medicineImageURL, command.validatedFileContent())
        medicineImageStorage.upload(medicineImage)

        return medicineImageURL
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

    private fun findMedicineOwnedBy(medicineId: MedicineId, userSession: UserSession): Medicine? {
        val medicine = medicineRepository.findById(medicineId) ?: return null
        return if (medicine.isOwnedBy(userSession.accountId)) medicine else null
    }
}