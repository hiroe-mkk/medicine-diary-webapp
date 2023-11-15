package example.infrastructure.repository.medicine

import example.domain.model.medicine.*
import example.infrastructure.repository.shared.*
import org.apache.ibatis.annotations.*
import java.time.*

@Mapper
interface MedicineMapper {
    fun findOneByMedicineId(medicineId: String): MedicineResultEntity?

    fun findAllByAccountId(accountId: String?): Set<MedicineResultEntity>

    fun findAllByAccountIds(accountIds: Collection<String>): Set<MedicineResultEntity>

    fun findAllBySharedGroupId(sharedGroupId: String?): Set<MedicineResultEntity>

    fun upsertOneMedicine(medicineId: String,
                          accountId: String?,
                          sharedGroupId: String?,
                          medicineName: String,
                          quantity: Double,
                          takingUnit: String,
                          timesPerDay: Int,
                          precautions: String,
                          medicineImageURLEndpoint: String?,
                          medicineImageURLPath: String?,
                          isPublic: Boolean,
                          registeredAt: LocalDateTime)

    fun insertAllTimingOptions(medicineId: String,
                               timingOptions: Collection<OrderedEntity<Timing>>)

    fun insertAllEffects(medicineId: String,
                         effects: Collection<OrderedEntity<String>>)

    fun deleteOneMedicineByMedicineId(medicineId: String)

    fun deleteAllTimingOptionsByMedicineId(medicineId: String)

    fun deleteAllEffectsByMedicineId(medicineId: String)

    fun deleteAllTimingOptionsByMedicineIds(medicineIds: List<String>)

    fun deleteAllEffectsByMedicineIds(medicineIds: List<String>)

    fun deleteAllMedicineBySharedGroupId(sharedGroupId: String)
}