package example.domain.model.medicine

import example.domain.model.account.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.medicineimage.*
import example.domain.model.sharedgroup.*
import java.time.*

/**
 * 薬
 */
class Medicine(val id: MedicineId,
               owner: MedicineOwner,
               medicineName: MedicineName,
               dosageAndAdministration: DosageAndAdministration,
               effects: Effects,
               precautions: String,
               medicineImageURL: MedicineImageURL?,
               isPublic: Boolean,
               inventory: Inventory?,
               val registeredAt: LocalDateTime) {
    var owner: MedicineOwner = owner
        private set
    var medicineName: MedicineName = medicineName
        private set
    var dosageAndAdministration: DosageAndAdministration = dosageAndAdministration
        private set
    var effects: Effects = effects
        private set
    var precautions: String = precautions
        private set
    var medicineImageURL: MedicineImageURL? = medicineImageURL
        private set
    var isPublic: Boolean = isPublic
        private set
    var inventory: Inventory? = inventory
        private set

    companion object {
        fun create(id: MedicineId,
                   owner: MedicineOwner,
                   medicineName: MedicineName,
                   dosageAndAdministration: DosageAndAdministration,
                   effects: Effects,
                   precautions: String,
                   isPublic: Boolean,
                   registeredAt: LocalDateTime): Medicine {
            return Medicine(id,
                            owner,
                            medicineName,
                            dosageAndAdministration,
                            effects,
                            precautions,
                            null,
                            if (owner.isSharedGroup) true else isPublic,
                            null,
                            registeredAt)
        }
    }

    fun isOwnedBy(accountId: AccountId): Boolean = owner.accountId == accountId

    fun isOwnedBy(sharedGroupId: SharedGroupId): Boolean = owner.sharedGroupId == sharedGroupId

    fun changeBasicInfo(medicineName: MedicineName,
                        dosageAndAdministration: DosageAndAdministration,
                        effects: Effects,
                        precautions: String,
                        isPublic: Boolean) {
        this.medicineName = medicineName
        this.dosageAndAdministration = dosageAndAdministration
        this.effects = effects
        this.precautions = precautions
        this.isPublic = if (owner.isSharedGroup) true else isPublic
    }

    fun changeOwner(newOwner: MedicineOwner) {
        this.owner = newOwner
        this.isPublic = if (newOwner.isSharedGroup) true else isPublic
    }

    fun changeMedicineImage(medicineImageURL: MedicineImageURL) {
        this.medicineImageURL = medicineImageURL
    }

    fun deleteMedicineImage() {
        this.medicineImageURL = null
    }

    fun adjustInventory(inventory: Inventory) {
        this.inventory = inventory
    }

    fun stopInventoryManagement() {
        this.inventory = null
    }

    fun clone(newMedicineId: MedicineId, newOwner: MedicineOwner, newMedicineImageURL: MedicineImageURL?): Medicine {
        return Medicine(newMedicineId,
                        newOwner,
                        this.medicineName,
                        this.dosageAndAdministration,
                        this.effects,
                        this.precautions,
                        newMedicineImageURL,
                        this.isPublic,
                        this.inventory,
                        this.registeredAt)
    }

    fun taken(medicationRecordId: MedicationRecordId,
              recorder: AccountId,
              dose: Dose,
              followUp: FollowUp,
              note: String,
              takenOn: LocalDate,
              takenAt: LocalTime,
              symptomOnsetAt: LocalTime?,
              onsetEffectAt: LocalTime?): MedicationRecord {
        this.inventory = this.inventory?.decrease(dose)
        return MedicationRecord(medicationRecordId,
                                recorder,
                                id,
                                dose,
                                followUp,
                                note,
                                takenOn,
                                takenAt,
                                symptomOnsetAt,
                                onsetEffectAt)
    }
}
