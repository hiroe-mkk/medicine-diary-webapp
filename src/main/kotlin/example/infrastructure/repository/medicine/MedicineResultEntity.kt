package example.infrastructure.repository.medicine

import example.domain.model.account.*
import example.domain.model.medicine.*
import example.domain.shared.type.*
import example.infrastructure.repository.shared.*
import java.time.*

class MedicineResultEntity(val medicineId: MedicineId,
                           val owner: AccountId,
                           val name: String,
                           val dosage: Dosage,
                           val timesPerDay: Int,
                           val precautions: Note,
                           val registeredAt: LocalDateTime) {
    // MyBatis ではコレクションをコンストラクタに渡すことができない
    val timingOptions: List<OrderedEntity<Timing>> = mutableListOf()
    val effects: List<OrderedEntity<String>> = mutableListOf()

    fun toMedicine(): Medicine {
        return Medicine(medicineId,
                        owner,
                        name,
                        dosage,
                        Administration(timesPerDay,
                                       OrderedEntitiesConverter.restore(timingOptions)),
                        Effects(OrderedEntitiesConverter.restore(effects)),
                        precautions,
                        registeredAt)
    }
}