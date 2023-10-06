package example.infrastructure.repository.medicine

import example.domain.model.medicine.*
import example.infrastructure.repository.shared.*
import org.apache.ibatis.annotations.*
import java.time.*

@Mapper
interface MedicineMapper {
    fun findOneByMedicineId(medicineId: String): MedicineResultEntity?

    fun findAllByAccountId(accountId: String): List<MedicineResultEntity>

    fun saveOneMedicine(medicineId: String,
                        owner: String,
                        name: String,
                        takingUnit: String,
                        quantity: Double,
                        timesPerDay: Int,
                        precautions: String,
                        registeredAt: LocalDateTime)

    fun saveAllTimingOptions(medicineId: String,
                             timingOptions: Collection<OrderedEntity<Timing>>)

    fun saveAllEffects(medicineId: String,
                       effects: Collection<OrderedEntity<String>>)

    fun deleteOneMedicine(medicineId: String)

    fun deleteAllTimingOptions(medicineId: String)
    fun deleteAllEffects(medicineId: String)
}