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
               medicineName: MedicineName,
               dosageAndAdministration: DosageAndAdministration,
               effects: Effects,
               precautions: Note,
               medicineImageURL: MedicineImageURL?,
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

    companion object {
        fun create(id: MedicineId,
                   owner: AccountId,
                   medicineName: MedicineName,
                   dosageAndAdministration: DosageAndAdministration,
                   effects: Effects,
                   precautions: Note,
                   registeredAt: LocalDateTime): Medicine {
            return Medicine(id,
                            owner,
                            medicineName,
                            dosageAndAdministration,
                            effects,
                            precautions,
                            null,
                            registeredAt)
        }
    }

    fun isOwnedBy(accountId: AccountId): Boolean = owner == accountId

    fun changeBasicInfo(medicineName: MedicineName,
                        dosageAndAdministration: DosageAndAdministration,
                        effects: Effects,
                        precautions: Note) {
        this.medicineName = medicineName
        this.dosageAndAdministration = dosageAndAdministration
        this.effects = effects
        this.precautions = precautions
    }

    fun changeMedicineImage(medicineImageURL: MedicineImageURL) {
        this.medicineImageURL = medicineImageURL
    }
}