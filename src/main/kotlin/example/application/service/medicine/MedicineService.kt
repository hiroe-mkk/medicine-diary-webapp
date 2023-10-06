package example.application.service.medicine

import example.application.shared.usersession.*
import example.domain.model.medicine.*
import example.domain.shared.type.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class MedicineService(private val medicineRepository: MedicineRepository,
                      private val localDateTimeProvider: LocalDateTimeProvider) {
    /**
     * 薬を取得する
     */
    @Transactional(readOnly = true)
    fun findMedicine(medicineId: MedicineId, userSession: UserSession): MedicineDto {
        val medicine = findMedicineOwnedBy(medicineId, userSession) ?: throw MedicineNotFoundException(medicineId)
        return MedicineDto.from(medicine)
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
        val medicine = Medicine(medicineRepository.createMedicineId(),
                                userSession.accountId,
                                medicineBasicInfoInputCommand.validatedName,
                                medicineBasicInfoInputCommand.validatedTakingUnit,
                                medicineBasicInfoInputCommand.validatedDosage,
                                medicineBasicInfoInputCommand.validatedAdministration,
                                medicineBasicInfoInputCommand.validatedEffects,
                                medicineBasicInfoInputCommand.validatedPrecautions,
                                localDateTimeProvider.now())
        medicineRepository.save(medicine)
        return medicine.id
    }

    private fun findMedicineOwnedBy(medicineId: MedicineId, userSession: UserSession): Medicine? {
        val medicine = medicineRepository.findById(medicineId) ?: return null
        return if (medicine.isOwnedBy(userSession.accountId)) medicine else null
    }
}