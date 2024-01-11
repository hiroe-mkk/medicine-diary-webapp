package example.domain.model.medicine.medicineimage

import example.domain.shared.type.*

interface MedicineImageStorage {
    fun createURL(): MedicineImageURL

    fun upload(medicineImageURL: MedicineImageURL, fileContent: FileContent)

    fun copy(sourceMedicineImageURL: MedicineImageURL, targetMedicineImageUrl: MedicineImageURL)

    fun delete(medicineImageURL: MedicineImageURL)

    fun deleteAll(medicineImageURLs: Collection<MedicineImageURL>)
}