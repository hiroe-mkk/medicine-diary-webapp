package example.infrastructure.repository.medicine

import example.domain.model.account.*
import example.domain.model.medicine.*
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

    override fun findByAccountId(accountId: AccountId): List<Medicine> {
        return medicineMapper.findAllByAccountId(accountId.value).map { it.toMedicine() }
    }

    override fun save(medicine: Medicine) {
        medicineMapper.deleteAllTimingOptions(medicine.id.value)
        medicineMapper.deleteAllEffects(medicine.id.value)

        medicineMapper.upsertOneMedicine(medicine.id.value,
                                         medicine.owner.value,
                                         medicine.name,
                                         medicine.dosage.quantity,
                                         medicine.dosage.takingUnit,
                                         medicine.administration.timesPerDay,
                                         medicine.precautions.value,
                                         medicine.registeredAt)
        saveAllTimingOptions(medicine)
        saveAllEffects(medicine)
    }

    private fun saveAllTimingOptions(medicine: Medicine) {
        val timingOptions = medicine.administration.timingOptions
        if (timingOptions.isEmpty()) return

        medicineMapper.insertAllTimingOptions(medicine.id.value,
                                              OrderedEntitiesConverter.convert(timingOptions))
    }

    private fun saveAllEffects(medicine: Medicine) {
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