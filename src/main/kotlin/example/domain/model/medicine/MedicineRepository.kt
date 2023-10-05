package example.domain.model.medicine

interface MedicineRepository {
    fun findById(medicineId: MedicineId): Medicine?

    fun save(medicine: Medicine)
}