package example.infrastructure.repository.medicine

import example.domain.model.account.*
import example.domain.model.medicine.*
import example.domain.model.medicine.medicineImage.*
import example.domain.shared.type.*
import example.infrastructure.repository.shared.*
import java.time.*

class MedicineResultEntity(val medicineId: MedicineId,
                           val owner: AccountId,
                           val medicineName: MedicineName,
                           val dose: Dose,
                           val takingUnit: String,
                           val timesPerDay: Int,
                           val precautions: Note,
                           val medicineImageURL: MedicineImageURL?,
                           val registeredAt: LocalDateTime) {
    // MyBatis ではコレクションをコンストラクタに渡すことができない
    val timingOptions: List<OrderedEntity<Timing>> = mutableListOf()
    val effects: List<OrderedEntity<String>> = mutableListOf()

    fun toMedicine(): Medicine {
        return Medicine(medicineId,
                        owner,
                        medicineName,
                        DosageAndAdministration(dose,
                                                takingUnit,
                                                timesPerDay,
                                                OrderedEntitiesConverter.restore(timingOptions)),
                        Effects(OrderedEntitiesConverter.restore(effects)),
                        precautions,
                        medicineImageURL,
                        registeredAt)
    }
}