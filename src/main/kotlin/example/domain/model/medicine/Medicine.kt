package example.domain.model.medicine

import example.domain.model.account.*
import example.domain.model.medicine.medicineImage.*
import example.domain.shared.type.*
import java.time.*

/**
 * 薬
 */
class Medicine(val id: MedicineId,
               val owner: AccountId,
               name: String,
               dosageAndAdministration: DosageAndAdministration,
               effects: Effects,
               precautions: Note,
               medicineImageURL: MedicineImageURL?,
               val registeredAt: LocalDateTime) {
    var name: String = name
        private set
    var dosageAndAdministration: DosageAndAdministration = dosageAndAdministration
        private set
    var effects: Effects = effects
        private set
    var precautions: Note = precautions
        private set
    var medicineImageURL: MedicineImageURL? = medicineImageURL
        private set

    companion object {
        fun create(id: MedicineId,
                   owner: AccountId,
                   name: String,
                   dosageAndAdministration: DosageAndAdministration,
                   effects: Effects,
                   precautions: Note,
                   registeredAt: LocalDateTime): Medicine {
            return Medicine(id,
                            owner,
                            name,
                            dosageAndAdministration,
                            effects,
                            precautions,
                            null,
                            registeredAt)
        }
    }

    fun isOwnedBy(accountId: AccountId): Boolean = owner == accountId

    fun changeBasicInfo(newName: String,
                        newDosageAndAdministration: DosageAndAdministration,
                        newEffects: Effects,
                        newPrecautions: Note) {
        this.name = newName
        this.dosageAndAdministration = newDosageAndAdministration
        this.effects = newEffects
        this.precautions = newPrecautions
    }

    fun changeMedicineImage(medicineImageURL: MedicineImageURL) {
        this.medicineImageURL = medicineImageURL
    }
}