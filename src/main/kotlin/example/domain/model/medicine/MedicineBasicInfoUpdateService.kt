package example.domain.model.medicine

import example.application.service.medicine.*
import example.domain.model.account.*
import example.domain.model.medicationrecord.*
import example.domain.shared.type.*
import org.springframework.stereotype.*

@Component
class MedicineBasicInfoUpdateService(private val medicineRepository: MedicineRepository,
                                     private val medicationRecordRepository: MedicationRecordRepository,
                                     private val medicineOwnerCreationService: MedicineOwnerCreationService,
                                     private val medicineQueryService: MedicineQueryService) {
    fun update(id: MedicineId,
               medicineName: MedicineName,
               dosageAndAdministration: DosageAndAdministration,
               effects: Effects,
               precautions: Note,
               isOwnedBySharedGroup: Boolean,
               isPublic: Boolean,
               registrant: AccountId) {
        val medicine = medicineQueryService.findAvailableMedicine(id, registrant)
                       ?: throw MedicineNotFoundException(id)

        if (isOwnedBySharedGroup) {
            if (medicine.owner.isSharedGroup) {
                medicine.changeBasicInfo(medicineName, dosageAndAdministration, effects, precautions, isPublic)
                medicineRepository.save(medicine)
            } else {
                changeOwnerFromAccountToSharedGroup(medicine,
                                                    medicineName,
                                                    dosageAndAdministration,
                                                    effects,
                                                    precautions,
                                                    isPublic,
                                                    registrant)
            }
        } else {
            if (medicine.owner.isAccount) {
                medicine.changeBasicInfo(medicineName, dosageAndAdministration, effects, precautions, isPublic)
                medicineRepository.save(medicine)
            } else {
                changeOwnerFromSharedGroupToAccount(medicine,
                                                    medicineName,
                                                    dosageAndAdministration,
                                                    effects,
                                                    precautions,
                                                    isPublic,
                                                    registrant)
            }
        }
    }

    private fun changeOwnerFromAccountToSharedGroup(registrantMedicine: Medicine,
                                                    newMedicineName: MedicineName,
                                                    newDosageAndAdministration: DosageAndAdministration,
                                                    newEffects: Effects,
                                                    newPrecautions: Note,
                                                    newIsPublic: Boolean,
                                                    registrant: AccountId) {
        changeOwnerAndUpdateBasicInfo(registrantMedicine,
                                      newMedicineName,
                                      newDosageAndAdministration,
                                      newEffects,
                                      newPrecautions,
                                      newIsPublic,
                                      medicineOwnerCreationService.createSharedGroupOwner(registrant))
    }

    private fun changeOwnerFromSharedGroupToAccount(sharedGroupMedicine: Medicine,
                                                    newMedicineName: MedicineName,
                                                    newDosageAndAdministration: DosageAndAdministration,
                                                    newEffects: Effects,
                                                    newPrecautions: Note,
                                                    newIsPublic: Boolean,
                                                    registrant: AccountId) {
        changeOwnerAndUpdateBasicInfo(sharedGroupMedicine,
                                      newMedicineName,
                                      newDosageAndAdministration,
                                      newEffects,
                                      newPrecautions,
                                      newIsPublic,
                                      medicineOwnerCreationService.createAccountOwner(registrant))
        val medicationRecordsByOthers = medicationRecordRepository.findByTakenMedicine(sharedGroupMedicine.id)
            .filterNot { it.recorder == registrant }
            .groupBy { it.recorder }
        if (medicationRecordsByOthers.isEmpty()) return

        medicationRecordsByOthers.keys.forEach { recorder ->
            val newMedicineOwner = medicineOwnerCreationService.createAccountOwner(recorder)
            val newMedicine = Medicine(medicineRepository.createMedicineId(),
                                       newMedicineOwner,
                                       newMedicineName,
                                       newDosageAndAdministration,
                                       newEffects,
                                       newPrecautions,
                                       sharedGroupMedicine.medicineImageURL,
                                       newIsPublic,
                                       sharedGroupMedicine.inventory,
                                       sharedGroupMedicine.registeredAt)
            medicineRepository.save(newMedicine)

            medicationRecordsByOthers[recorder]?.forEach { it.changeTakenMedicine(newMedicine.id) }
            medicationRecordRepository.saveAll(medicationRecordsByOthers[recorder]!!)
        }
    }

    private fun changeOwnerAndUpdateBasicInfo(sharedGroupMedicine: Medicine,
                                              newMedicineName: MedicineName,
                                              newDosageAndAdministration: DosageAndAdministration,
                                              newEffects: Effects,
                                              newPrecautions: Note,
                                              newIsPublic: Boolean,
                                              newMedicineOwner: MedicineOwner) {
        sharedGroupMedicine.changeOwner(newMedicineOwner)
        sharedGroupMedicine.changeBasicInfo(newMedicineName,
                                            newDosageAndAdministration,
                                            newEffects,
                                            newPrecautions,
                                            newIsPublic)
        medicineRepository.save(sharedGroupMedicine)
    }
}