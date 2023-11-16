package example.infrastructure.repository.medicine

import example.domain.model.account.*
import example.domain.model.medicine.*
import example.domain.model.sharedgroup.*
import example.infrastructure.repository.shared.*
import org.springframework.stereotype.*
import java.util.*

@Repository
class MyBatisMedicineRepository(private val medicineMapper: MedicineMapper) : MedicineRepository {
    override fun createMedicineId(): MedicineId {
        return MedicineId(UUID.randomUUID().toString())
    }

    override fun findById(medicineId: MedicineId): Medicine? {
        return medicineMapper.findOneByMedicineId(medicineId.value)?.toMedicine()
    }

    override fun findByOwner(accountId: AccountId): Set<Medicine> {
        return medicineMapper.findAllByAccountId(accountId.value).map { it.toMedicine() }.toSet()
    }

    override fun findByOwners(accountIds: Collection<AccountId>): Set<Medicine> {
        if (accountIds.isEmpty()) return emptySet()
        return medicineMapper.findAllByAccountIds(accountIds.map { it.value }).map { it.toMedicine() }.toSet()
    }

    override fun findByOwner(sharedGroupId: SharedGroupId): Set<Medicine> {
        return medicineMapper.findAllBySharedGroupId(sharedGroupId.value).map { it.toMedicine() }.toSet()
    }

    override fun save(medicine: Medicine) {
        medicineMapper.upsertOneMedicine(medicine.id.value,
                                         medicine.owner.accountId?.value,
                                         medicine.owner.sharedGroupId?.value,
                                         medicine.medicineName.value,
                                         medicine.dosageAndAdministration.dose.quantity,
                                         medicine.dosageAndAdministration.doseUnit,
                                         medicine.dosageAndAdministration.timesPerDay,
                                         medicine.precautions.value,
                                         medicine.medicineImageURL?.endpoint,
                                         medicine.medicineImageURL?.path,
                                         medicine.isPublic,
                                         medicine.registeredAt)
        upsertInventory(medicine)
        upsertAllTimingOptions(medicine)
        upsertAllEffects(medicine)
    }

    private fun upsertInventory(medicine: Medicine) {
        medicineMapper.deleteOneInventoryByMedicineId(medicine.id.value)
        val inventory = medicine.inventory ?: return

        medicineMapper.insertOneInventory(medicine.id.value,
                                          inventory.remainingQuantity,
                                          inventory.quantityPerPackage,
                                          inventory.startedOn,
                                          inventory.expirationOn,
                                          inventory.unusedPackage)
    }

    private fun upsertAllTimingOptions(medicine: Medicine) {
        medicineMapper.deleteAllTimingOptionsByMedicineId(medicine.id.value)
        val timingOptions = medicine.dosageAndAdministration.timingOptions
        if (timingOptions.isEmpty()) return

        medicineMapper.insertAllTimingOptions(medicine.id.value,
                                              OrderedEntitiesConverter.convert(timingOptions))
    }

    private fun upsertAllEffects(medicine: Medicine) {
        medicineMapper.deleteAllEffectsByMedicineId(medicine.id.value)
        val effects = medicine.effects.values
        if (effects.isEmpty()) return

        medicineMapper.insertAllEffects(medicine.id.value,
                                        OrderedEntitiesConverter.convert(effects))
    }

    override fun deleteById(medicineId: MedicineId) {
        medicineMapper.deleteOneInventoryByMedicineId(medicineId.value)
        medicineMapper.deleteAllTimingOptionsByMedicineId(medicineId.value)
        medicineMapper.deleteAllEffectsByMedicineId(medicineId.value)
        medicineMapper.deleteOneMedicineByMedicineId(medicineId.value)
    }

    override fun deleteByOwner(accountId: AccountId) {
        val medicineIds = findByOwner(accountId).map { it.id.value }
        if (medicineIds.isEmpty()) return

        medicineMapper.deleteAllInventoriesByMedicineIds(medicineIds)
        medicineMapper.deleteAllTimingOptionsByMedicineIds(medicineIds)
        medicineMapper.deleteAllEffectsByMedicineIds(medicineIds)
        medicineMapper.deleteAllMedicineByAccountId(accountId.value)
    }

    override fun deleteByOwner(sharedGroupId: SharedGroupId) {
        val medicineIds = findByOwner(sharedGroupId).map { it.id.value }
        if (medicineIds.isEmpty()) return

        medicineMapper.deleteAllInventoriesByMedicineIds(medicineIds)
        medicineMapper.deleteAllTimingOptionsByMedicineIds(medicineIds)
        medicineMapper.deleteAllEffectsByMedicineIds(medicineIds)
        medicineMapper.deleteAllMedicinesBySharedGroupId(sharedGroupId.value)
    }
}