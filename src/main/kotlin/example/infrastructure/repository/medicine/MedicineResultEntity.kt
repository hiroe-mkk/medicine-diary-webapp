package example.infrastructure.repository.medicine

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
    // MyBatis ではコレクションをコンストラクタに渡すことができない
    val timingOptions: List<OrderedEntity<Timing>> = mutableListOf()
    val effects: List<OrderedEntity<String>> = mutableListOf()

    fun toMedicine(): Medicine {
        return Medicine(medicineId,
                        owner,
                        medicineName,
                        DosageAndAdministration(dose,
                                                doseUnit,
                                                timesPerDay,
                                                OrderedEntitiesConverter.restore(timingOptions)),
                        Effects(OrderedEntitiesConverter.restore(effects)),
                        precautions,
                        medicineImageURL,
                        isPublic,
                        inventory,
                        registeredAt)
    }
}