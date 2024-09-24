package example.application.service.medicine

import example.application.shared.usersession.*
import example.domain.model.medicine.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class MedicineDeletionService(private val medicineDeletionCoordinator: MedicineDeletionCoordinator) {
    /**
     * 薬を削除する
     */
    fun deleteMedicine(medicineId: MedicineId, userSession: UserSession) {
        medicineDeletionCoordinator.deleteOwnedMedicineAndMedicationRecords(medicineId, userSession.accountId)
    }
}
