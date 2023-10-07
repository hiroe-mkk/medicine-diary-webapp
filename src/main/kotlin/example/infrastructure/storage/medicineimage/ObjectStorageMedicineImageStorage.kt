package example.infrastructure.storage.medicineimage

import example.domain.model.medicine.medicineImage.*
import example.infrastructure.storage.shared.objectstrage.*
import org.springframework.stereotype.*
import java.util.*

@Component
class ObjectStorageMedicineImageStorage(private val objectStorageClient: ObjectStorageClient) : MedicineImageStorage {
    override fun createURL(): MedicineImageURL {
        val path = "/medicineimage/${UUID.randomUUID()}"
        return MedicineImageURL(objectStorageClient.getEndpoint(), path)
    }

    override fun upload(medicineImage: MedicineImage) {
        objectStorageClient.put(medicineImage.path, medicineImage.fileContent)
    }

    override fun delete(medicineImageURL: MedicineImageURL) {
        objectStorageClient.remove(medicineImageURL)
    }
}