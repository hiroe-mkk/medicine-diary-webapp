package example.infrastructure.db.repository.medicine

import example.domain.model.medicine.*
import example.domain.model.medicine.medicineimage.*
import example.infrastructure.repository.shared.*
import java.time.*

class MedicineResultEntity(val medicineId: MedicineId,
                           val owner: MedicineOwner,
                           val medicineName: MedicineName,
                           val dose: Dose,
                           val doseUnit: String,
                           val timesPerDay: Int,
                           val precautions: String,
                           val medicineImageURL: MedicineImageURL?,
                           val isPublic: Boolean,
                           val inventory: Inventory?,
                           val registeredAt: LocalDateTime) {
    private lateinit var timingOptionsForMapping: List<OrderedEntity<Timing>>
    val timingOptions: DosageAndAdministration
        get() = DosageAndAdministration(dose,
                                        doseUnit,
                                        timesPerDay,
                                        example.infrastructure.db.repository.shared.OrderedEntitiesConverter.restore(timingOptionsForMapping))

    private lateinit var effectsForMapping: List<OrderedEntity<String>>
    val effects: Effects
        get() = Effects(example.infrastructure.db.repository.shared.OrderedEntitiesConverter.restore(effectsForMapping))

    fun toMedicine(): Medicine {
        return Medicine(medicineId,
                        owner,
                        medicineName,
                        timingOptions,
                        effects,
                        precautions,
                        medicineImageURL,
                        isPublic,
                        inventory,
                        registeredAt)
    }
}
