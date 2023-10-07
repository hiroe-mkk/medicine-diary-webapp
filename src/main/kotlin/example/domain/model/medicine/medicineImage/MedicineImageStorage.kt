package example.domain.model.medicine.medicineImage

interface MedicineImageStorage {
    fun createURL(): MedicineImageURL

    fun upload(medicineImage: MedicineImage)

    fun delete(medicineImageURL: MedicineImageURL)
}