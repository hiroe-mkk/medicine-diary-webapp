package example.domain.model.medicine

import example.application.service.medicine.*
import example.domain.model.account.*
import example.domain.model.medicationrecord.*
import org.springframework.stereotype.*

@Component
class MedicineOwnerChangeService(private val medicineRepository: MedicineRepository,
                                 private val medicationRecordRepository: MedicationRecordRepository,
                                 private val medicineQueryService: MedicineQueryService) {
    fun changeOwner(medicineId: MedicineId, newOwner: MedicineOwner, accountId: AccountId) {
        val medicine = medicineQueryService.findAvailableMedicine(medicineId, accountId)
                       ?: throw MedicineNotFoundException(medicineId)
        if (medicine.owner == newOwner) return

        if (newOwner.isSharedGroup) {
            changeOwnerFromAccountToSharedGroup(medicine, newOwner)
        } else {
            changeOwnerFromSharedGroupToAccount(medicine, newOwner)
        }
    }

    private fun changeOwnerFromAccountToSharedGroup(medicine: Medicine, newOwner: MedicineOwner) {
        medicine.changeOwner(newOwner)
        medicineRepository.save(medicine)
    }

    private fun changeOwnerFromSharedGroupToAccount(medicine: Medicine, newOwner: MedicineOwner) {
        val recorderToMedicationRecords =
                medicationRecordRepository.findByTakenMedicine(medicine.id).groupBy { it.recorder }

        if (recorderToMedicationRecords.isEmpty() || recorderToMedicationRecords.size == 1) {
            medicine.changeOwner(newOwner)
            medicineRepository.save(medicine)
        } else {
            recorderToMedicationRecords.keys.forEach { recorder ->
                val copiedMedicine = medicine.cloneWithMedicineIdAndOwner(medicineRepository.createMedicineId(),
                                                                          MedicineOwner.create(recorder))
                medicineRepository.save(copiedMedicine)

                recorderToMedicationRecords[recorder]!!.forEach { it.changeTakenMedicine(copiedMedicine.id) }
                medicationRecordRepository.saveAll(recorderToMedicationRecords[recorder]!!)
            }

            medicineRepository.deleteById(medicine.id)
        }
    }
}