package example.application.service.medicine

import example.application.shared.usersession.*
import example.domain.model.medicine.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional(readOnly = true)
class MedicineQueryService(private val medicineRepository: MedicineRepository,
                           private val medicineFinder: MedicineFinder) {
    /**
     * 薬を取得する
     */
    fun findMedicine(medicineId: MedicineId, userSession: UserSession): MedicineDto {
        val medicine = medicineFinder.findViewableMedicine(medicineId, userSession.accountId)
                       ?: throw MedicineNotFoundException(medicineId)
        return MedicineDto.from(medicine)
    }

    /**
     * 有効な薬 ID か
     */
    fun isValidMedicineId(medicineId: MedicineId): Boolean {
        return medicineRepository.isValidMedicineId(medicineId)
    }

    /**
     * 服用可能な薬か
     */
    fun isAvailableMedicine(medicineId: MedicineId, userSession: UserSession): Boolean {
        return medicineFinder.isAvailableMedicine(medicineId, userSession.accountId)
    }
}
