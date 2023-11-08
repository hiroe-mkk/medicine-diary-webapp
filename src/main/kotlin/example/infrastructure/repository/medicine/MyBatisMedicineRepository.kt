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

    override fun findByAccountId(accountId: AccountId): Set<Medicine> {
        return medicineMapper.findAllByAccountId(accountId.value).map { it.toMedicine() }.toSet()
    }

    override fun findByAccountIds(accountIds: Collection<AccountId>): Set<Medicine> {
        return medicineMapper.findAllByAccountIds(accountIds.map { it.value }).map { it.toMedicine() }.toSet()
    }

    override fun findBySharedGroupId(sharedGroupId: SharedGroupId): Set<Medicine> {
        return medicineMapper.findAllBySharedGroupId(sharedGroupId.value).map { it.toMedicine() }.toSet()
    }

    override fun save(medicine: Medicine) {
        medicineMapper.upsertOneMedicine(medicine.id.value,
                                         medicine.owner.accountId?.value,
                                         medicine.owner.sharedGroupId?.value,
                                         medicine.medicineName.value,
                                         medicine.dosageAndAdministration.dose.quantity,
                                         medicine.dosageAndAdministration.takingUnit,
                                         medicine.dosageAndAdministration.timesPerDay,
                                         medicine.precautions.value,
                                         medicine.medicineImageURL?.endpoint,
                                         medicine.medicineImageURL?.path,
                                         medicine.isPublic,
                                         medicine.registeredAt)
        upsertAllTimingOptions(medicine)
        upsertAllEffects(medicine)
    }

    private fun upsertAllTimingOptions(medicine: Medicine) {
        medicineMapper.deleteAllTimingOptions(medicine.id.value)
        val timingOptions = medicine.dosageAndAdministration.timingOptions
        if (timingOptions.isEmpty()) return

        medicineMapper.insertAllTimingOptions(medicine.id.value,
                                              OrderedEntitiesConverter.convert(timingOptions))
    }

    private fun upsertAllEffects(medicine: Medicine) {
        medicineMapper.deleteAllEffects(medicine.id.value)
        val effects = medicine.effects.values
        if (effects.isEmpty()) return

        medicineMapper.insertAllEffects(medicine.id.value,
                                        OrderedEntitiesConverter.convert(effects))
    }

    override fun delete(medicineId: MedicineId) {
        medicineMapper.deleteAllTimingOptions(medicineId.value)
        medicineMapper.deleteAllEffects(medicineId.value)
        medicineMapper.deleteOneMedicine(medicineId.value)
    }
}