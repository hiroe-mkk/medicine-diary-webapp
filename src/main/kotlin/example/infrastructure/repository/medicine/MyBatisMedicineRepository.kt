package example.infrastructure.repository.medicine

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

    override fun save(medicine: Medicine) {
        medicineMapper.saveOneMedicine(medicine.id.value,
                                       medicine.owner.value,
                                       medicine.name,
                                       medicine.takingUnit,
                                       medicine.dosage.quantity,
                                       medicine.administration.timesPerDay,
                                       medicine.precautions,
                                       medicine.registeredAt)
        saveAllTimingOptions(medicine)
        saveAllEffects(medicine)
    }

    private fun saveAllTimingOptions(medicine: Medicine) {
        val timingOptions = medicine.administration.timingOptions
        if (timingOptions.isEmpty()) return

        medicineMapper.saveAllTimingOptions(medicine.id.value,
                                            OrderedEntitiesConverter.convert(timingOptions))
    }

    private fun saveAllEffects(medicine: Medicine) {
        val effects = medicine.effects.values
        if (effects.isEmpty()) return

        medicineMapper.saveAllEffects(medicine.id.value,
                                      OrderedEntitiesConverter.convert(effects))
    }

    override fun delete(medicineId: MedicineId) {
        medicineMapper.deleteAllTimingOptions(medicineId.value)
        medicineMapper.deleteAllEffects(medicineId.value)
        medicineMapper.deleteOneMedicine(medicineId.value)
    }
}