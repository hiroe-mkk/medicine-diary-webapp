package example.domain.model.medicine

import example.application.service.medicine.*
import example.domain.model.account.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.medicineimage.*
import org.springframework.stereotype.*

@Component
class MedicineBasicInfoUpdater(private val medicineRepository: MedicineRepository,
                               private val medicationRecordRepository: MedicationRecordRepository,
                               private val medicineImageStorage: MedicineImageStorage,
                               private val medicineOwnerFactory: MedicineOwnerFactory,
                               private val medicineFinder: MedicineFinder) {
    fun update(id: MedicineId,
               medicineName: MedicineName,
               dosageAndAdministration: DosageAndAdministration,
               effects: Effects,
               precautions: String,
               isOwnedBySharedGroup: Boolean,
               isPublic: Boolean,
               registrant: AccountId) {
        val medicine = medicineFinder.findAvailableMedicine(id, registrant)
                       ?: throw MedicineNotFoundException(id)

        if (medicine.owner.isSharedGroup) {
            if (isOwnedBySharedGroup) {
                updateBasicInfo(medicine, medicineName, dosageAndAdministration, effects, precautions, isPublic)
            } else {
                changeOwnerFromSharedGroupToAccountAndUpdateBasicInfo(medicine,
                                                                      medicineName,
                                                                      dosageAndAdministration,
                                                                      effects,
                                                                      precautions,
                                                                      isPublic,
                                                                      registrant)
            }
        } else if (medicine.owner.isAccount) {
            if (isOwnedBySharedGroup) {
                changeOwnerFromAccountToSharedGroupAndUpdateBasicInfo(medicine,
                                                                      medicineName,
                                                                      dosageAndAdministration,
                                                                      effects,
                                                                      precautions,
                                                                      isPublic,
                                                                      registrant)
            } else {
                updateBasicInfo(medicine, medicineName, dosageAndAdministration, effects, precautions, isPublic)
            }
        }
    }

    private fun changeOwnerFromAccountToSharedGroupAndUpdateBasicInfo(registrantMedicine: Medicine,
                                                                      newMedicineName: MedicineName,
                                                                      newDosageAndAdministration: DosageAndAdministration,
                                                                      newEffects: Effects,
                                                                      newPrecautions: String,
                                                                      newIsPublic: Boolean,
                                                                      registrant: AccountId) {
        registrantMedicine.changeOwner(medicineOwnerFactory.create(registrant, true))
        updateBasicInfo(registrantMedicine,
                        newMedicineName,
                        newDosageAndAdministration,
                        newEffects,
                        newPrecautions,
                        newIsPublic)
    }

    private fun changeOwnerFromSharedGroupToAccountAndUpdateBasicInfo(sharedGroupMedicine: Medicine,
                                                                      newMedicineName: MedicineName,
                                                                      newDosageAndAdministration: DosageAndAdministration,
                                                                      newEffects: Effects,
                                                                      newPrecautions: String,
                                                                      newIsPublic: Boolean,
                                                                      registrant: AccountId) {
        sharedGroupMedicine.changeOwner(medicineOwnerFactory.create(registrant, false))
        updateBasicInfo(sharedGroupMedicine,
                        newMedicineName,
                        newDosageAndAdministration,
                        newEffects,
                        newPrecautions,
                        newIsPublic)

        val medicationRecordsByOthers = medicationRecordRepository.findByTakenMedicine(sharedGroupMedicine.id)
            .filterNot { it.recorder == registrant }
            .groupBy { it.recorder }
        if (medicationRecordsByOthers.isEmpty()) return

        medicationRecordsByOthers.keys.forEach { recorder ->
            val newMedicineOwner = medicineOwnerFactory.create(recorder, false)
            val newMedicineImageURL = sharedGroupMedicine.medicineImageURL?.let {
                val newMedicineImageURL = medicineImageStorage.createURL()
                medicineImageStorage.copy(it, newMedicineImageURL)
                newMedicineImageURL
            }
            val newMedicine = Medicine(medicineRepository.createMedicineId(),
                                       newMedicineOwner,
                                       newMedicineName,
                                       newDosageAndAdministration,
                                       newEffects,
                                       newPrecautions,
                                       newMedicineImageURL,
                                       newIsPublic,
                                       sharedGroupMedicine.inventory,
                                       sharedGroupMedicine.registeredAt)
            medicineRepository.save(newMedicine)

            medicationRecordsByOthers[recorder]?.forEach { it.changeTakenMedicine(newMedicine.id) }
            medicationRecordRepository.saveAll(medicationRecordsByOthers[recorder]!!)
        }
    }

    private fun updateBasicInfo(medicine: Medicine,
                                medicineName: MedicineName,
                                dosageAndAdministration: DosageAndAdministration,
                                effects: Effects,
                                precautions: String,
                                isPublic: Boolean) {
        medicine.changeBasicInfo(medicineName, dosageAndAdministration, effects, precautions, isPublic)
        medicineRepository.save(medicine)
    }
}
