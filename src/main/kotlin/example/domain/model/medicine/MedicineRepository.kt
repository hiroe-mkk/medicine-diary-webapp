package example.domain.model.medicine

interface MedicineRepository {
    fun createMedicineId(): MedicineId

    fun findById(medicineId: MedicineId): Medicine?

    fun save(medicine: Medicine)

    fun delete(medicineId: MedicineId)
}