package example.infrastructure.db.repository.medicine

import example.domain.model.medicine.*
import example.infrastructure.db.repository.shared.*
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
                          doseUnit: String,
                          timesPerDay: Int,
                          precautions: String,
                          medicineImageURLEndpoint: String?,
                          medicineImageURLPath: String?,
                          isPublic: Boolean,
                          registeredAt: LocalDateTime)

    fun insertOneInventory(medicineId: String,
                           remainingQuantity: Double,
                           quantityPerPackage: Double,
                           startedOn: LocalDate?,
                           expirationOn: LocalDate?,
                           unusedPackage: Int)

    fun insertAllTimingOptions(medicineId: String,
                               timingOptions: Collection<OrderedEntity<Timing>>)

    fun insertAllEffects(medicineId: String,
                         effects: Collection<OrderedEntity<String>>)

    fun deleteOneMedicineByMedicineId(medicineId: String)

    fun deleteAllMedicineByAccountId(accountId: String)

    fun deleteAllMedicinesBySharedGroupId(sharedGroupId: String)

    fun deleteOneInventoryByMedicineId(medicineId: String)

    fun deleteAllInventoriesByMedicineIds(medicineIds: Collection<String>)

    fun deleteAllEffectsByMedicineId(medicineId: String)

    fun deleteAllEffectsByMedicineIds(medicineIds: Collection<String>)

    fun deleteAllTimingOptionsByMedicineId(medicineId: String)

    fun deleteAllTimingOptionsByMedicineIds(medicineIds: Collection<String>)
}
