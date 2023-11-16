package example.domain.model.medicine

import example.domain.model.account.*
import example.domain.model.medicine.medicineImage.*
import example.domain.model.sharedgroup.*
import example.domain.shared.type.*
import java.time.*

/**
 * 薬
 */
class Medicine(val id: MedicineId,
               val owner: MedicineOwner,
               medicineName: MedicineName,
               dosageAndAdministration: DosageAndAdministration,
               effects: Effects,
               precautions: Note,
               medicineImageURL: MedicineImageURL?,
               isPublic: Boolean,
               inventory: Inventory?,
               val registeredAt: LocalDateTime) {
    var medicineName: MedicineName = medicineName
        private set
    var dosageAndAdministration: DosageAndAdministration = dosageAndAdministration
        private set
    var effects: Effects = effects
        private set
    var precautions: Note = precautions
        private set
    var medicineImageURL: MedicineImageURL? = medicineImageURL
        private set
    var isPublic: Boolean = isPublic
        private set
    var inventory: Inventory? = inventory

    fun isOwnedBy(accountId: AccountId): Boolean = owner.accountId == accountId

    fun isOwnedBy(sharedGroupId: SharedGroupId): Boolean = owner.sharedGroupId == sharedGroupId

    fun changeBasicInfo(medicineName: MedicineName,
                        dosageAndAdministration: DosageAndAdministration,
                        effects: Effects,
                        precautions: Note,
                        isPublic: Boolean) {
        this.medicineName = medicineName
        this.dosageAndAdministration = dosageAndAdministration
        this.effects = effects
        this.precautions = precautions
        this.isPublic = if (owner.isSharedGroup) true else isPublic
    }

    fun changeMedicineImage(medicineImageURL: MedicineImageURL) {
        this.medicineImageURL = medicineImageURL
    }
}