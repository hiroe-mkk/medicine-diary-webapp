package example.application.service.medicine

import example.application.shared.usersession.*
import example.domain.model.medicine.*
import example.domain.model.medicine.medicineImage.*
import example.domain.model.takingrecord.*
import example.domain.shared.type.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class MedicineService(private val medicineRepository: MedicineRepository,
                      private val medicineImageStorage: MedicineImageStorage,
                      private val takingRecordRepository: TakingRecordRepository,
                      private val localDateTimeProvider: LocalDateTimeProvider,
                      private val medicineDomainService: MedicineDomainService) {
    /**
     * 薬を取得する
     */
    @Transactional(readOnly = true)
    fun findMedicine(medicineId: MedicineId, userSession: UserSession): MedicineDto {
        val medicine = medicineDomainService.findViewableMedicine(medicineId, userSession.accountId)
                       ?: throw MedicineNotFoundException(medicineId)
        return MedicineDto.from(medicine)
    }

    /**
     * 薬概要一覧を取得する
     */
    @Transactional(readOnly = true)
    fun findMedicineOverviews(userSession: UserSession): MedicineOverviews {
        val ownedMedicines = medicineDomainService.findAllOwnedMedicines(userSession.accountId)
        val sharedGroupMedicines = medicineDomainService.findAllSharedGroupMedicines(userSession.accountId)
        val membersMedicines = medicineDomainService.findAllMembersMedicines(userSession.accountId)
        return MedicineOverviews(convertToSortedDtoList(ownedMedicines),
                                 convertToSortedDtoList(sharedGroupMedicines),
                                 convertToSortedDtoList(membersMedicines))
    }

    /**
     * 服用可能な薬概要一覧を取得する
     */
    @Transactional(readOnly = true)
    fun findAvailableMedicineOverviews(userSession: UserSession): List<MedicineOverviewDto> {
        val medicines = medicineDomainService.findAllAvailableMedicines(userSession.accountId)
        return convertToSortedDtoList(medicines)
    }

    /**
     * 所有している薬か
     */
    @Transactional(readOnly = true)
    fun isOwnedMedicine(medicineId: MedicineId, userSession: UserSession): Boolean {
        return medicineDomainService.isOwnedMedicine(medicineId, userSession.accountId)
    }

    /**
     * 服用可能な薬な薬か
     */
    @Transactional(readOnly = true)
    fun isAvailableMedicine(medicineId: MedicineId, userSession: UserSession): Boolean {
        return medicineDomainService.isAvailableMedicine(medicineId, userSession.accountId)
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
        val medicine = findAvailableMedicineOrElseThrowException(medicineId, userSession)
        return MedicineBasicInfoEditCommand.initialize(medicine)
    }

    /**
     * 薬基本情報を更新する
     */
    fun updateMedicineBasicInfo(medicineId: MedicineId,
                                command: MedicineBasicInfoEditCommand,
                                userSession: UserSession) {
        val medicine = findAvailableMedicineOrElseThrowException(medicineId, userSession)
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
        val medicine = medicineDomainService.findAvailableMedicine(medicineId, userSession.accountId) ?: return
        takingRecordRepository.deleteByMedicineId(medicineId)
        medicineRepository.delete(medicine.id)
        medicine.medicineImageURL?.let { medicineImageStorage.delete(it) }
    }

    private fun findAvailableMedicineOrElseThrowException(medicineId: MedicineId,
                                                          userSession: UserSession): Medicine {
        return medicineDomainService.findAvailableMedicine(medicineId, userSession.accountId)
               ?: throw MedicineNotFoundException(medicineId)
    }

    private fun convertToSortedDtoList(medicines: Set<Medicine>): List<MedicineOverviewDto> {
        return medicines.sortedByDescending { it.registeredAt }.map { MedicineOverviewDto.from(it) }
    }
}