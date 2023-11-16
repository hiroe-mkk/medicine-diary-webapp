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
                      private val medicineQueryService: MedicineQueryService,
                      private val medicineCreationService: MedicineCreationService,
                      private val medicineDeletionService: MedicineDeletionService) {
    /**
     * 薬を取得する
     */
    @Transactional(readOnly = true)
    fun findMedicine(medicineId: MedicineId, userSession: UserSession): MedicineDto {
        val medicine = medicineQueryService.findViewableMedicine(medicineId, userSession.accountId)
                       ?: throw MedicineNotFoundException(medicineId)
        return MedicineDto.from(medicine)
    }

    /**
     * 薬概要一覧を取得する
     */
    @Transactional(readOnly = true)
    fun findMedicineOverviews(userSession: UserSession): MedicineOverviews {
        val ownedMedicines = medicineQueryService.findAllOwnedMedicines(userSession.accountId)
        val sharedGroupMedicines = medicineQueryService.findAllSharedGroupMedicines(userSession.accountId)
        val membersMedicines = medicineQueryService.findAllMembersMedicines(userSession.accountId)
        return MedicineOverviews(convertToSortedDtoList(ownedMedicines),
                                 convertToSortedDtoList(sharedGroupMedicines),
                                 convertToSortedDtoList(membersMedicines))
    }

    /**
     * 服用可能な薬概要一覧を取得する
     */
    @Transactional(readOnly = true)
    fun findAvailableMedicineOverviews(userSession: UserSession): List<MedicineOverviewDto> {
        val medicines = medicineQueryService.findAllAvailableMedicines(userSession.accountId)
        return convertToSortedDtoList(medicines)
    }

    /**
     * 所有している薬か
     */
    @Transactional(readOnly = true)
    fun isOwnedMedicine(medicineId: MedicineId, userSession: UserSession): Boolean {
        return medicineQueryService.isOwnedMedicine(medicineId, userSession.accountId)
    }

    /**
     * 服用可能な薬な薬か
     */
    @Transactional(readOnly = true)
    fun isAvailableMedicine(medicineId: MedicineId, userSession: UserSession): Boolean {
        return medicineQueryService.isAvailableMedicine(medicineId, userSession.accountId)
    }

    /**
     * 登録用の薬基本情報編集コマンドを取得する
     */
    fun getRegistrationMedicineBasicInfoEditCommand(userSession: UserSession): MedicineBasicInfoEditCommand {
        return MedicineBasicInfoEditCommand.initialize()
    }

    /**
     * 薬を登録する
     */
    fun registerMedicine(command: MedicineBasicInfoEditCommand,
                         isWantToOwn: Boolean,
                         userSession: UserSession): MedicineId {
        val medicine = medicineCreationService.create(medicineRepository.createMedicineId(),
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
     * 更新用の薬基本情報編集コマンドを取得する
     */
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
        medicineDeletionService.delete(medicineId, userSession.accountId)
    }

    private fun findAvailableMedicineOrElseThrowException(medicineId: MedicineId,
                                                          userSession: UserSession): Medicine {
        return medicineQueryService.findAvailableMedicine(medicineId, userSession.accountId)
               ?: throw MedicineNotFoundException(medicineId)
    }

    private fun convertToSortedDtoList(medicines: Set<Medicine>): List<MedicineOverviewDto> {
        return medicines.sortedByDescending { it.registeredAt }.map { MedicineOverviewDto.from(it) }
    }
}