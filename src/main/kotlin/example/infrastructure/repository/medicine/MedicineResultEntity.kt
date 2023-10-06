package example.infrastructure.repository.medicine

import example.domain.model.account.*
import example.domain.model.medicine.*
import example.infrastructure.repository.shared.*
import java.time.*

class MedicineResultEntity(val medicineId: MedicineId,
                           val owner: AccountId,
                           val name: String,
                           val takingUnit: String,
                           val dosage: Dosage,
                           val timesPerDay: Int,
                           val precautions: String,
                           val registeredAt: LocalDateTime) {
    // MyBatis ではコレクションをコンストラクタに渡すことができない
    val timingOptions: List<OrderedEntity<Timing>> = mutableListOf()
    val effects: List<OrderedEntity<String>> = mutableListOf()

    fun toMedicine(): Medicine {
        return Medicine(medicineId,
                        owner,
                        name,
                        takingUnit,
                        dosage,
                        Administration(timesPerDay,
                                       OrderedEntitiesConverter.restore(timingOptions)),
                        Effects(OrderedEntitiesConverter.restore(effects)),
                        precautions,
                        registeredAt)
    }
}