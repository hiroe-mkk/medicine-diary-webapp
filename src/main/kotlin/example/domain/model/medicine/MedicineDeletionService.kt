package example.domain.model.medicine

import example.domain.model.account.*
import example.domain.model.medicine.medicineImage.*
import example.domain.model.takingrecord.*
import org.springframework.stereotype.*

@Component
class MedicineDeletionService(private val medicineRepository: MedicineRepository,
                              private val medicineImageStorage: MedicineImageStorage,
                              private val takingRecordRepository: TakingRecordRepository,
                              private val medicineQueryService: MedicineQueryService) {
    fun deleteOne(medicineId: MedicineId, accountId: AccountId) {
        val medicine = medicineQueryService.findAvailableMedicine(medicineId, accountId) ?: return
        takingRecordRepository.deleteByMedicineId(medicineId)
        medicineRepository.delete(medicine.id)
        medicine.medicineImageURL?.let { medicineImageStorage.delete(it) }
    }
}