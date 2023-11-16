package example.infrastructure.storage.medicineimage

import example.domain.model.medicine.medicineimage.*
import example.domain.shared.type.*
import example.infrastructure.storage.shared.objectstrage.*
import org.springframework.stereotype.*
import java.util.*

@Component
class ObjectStorageMedicineImageStorage(private val objectStorageClient: ObjectStorageClient) : MedicineImageStorage {
    override fun createURL(): MedicineImageURL {
        val path = "/medicineimage/${UUID.randomUUID()}"
        return MedicineImageURL(objectStorageClient.getEndpoint(), path)
    }

    override fun upload(medicineImageURL: MedicineImageURL, fileContent: FileContent) {
        objectStorageClient.put(medicineImageURL, fileContent)
    }

    override fun delete(medicineImageURL: MedicineImageURL) {
        objectStorageClient.remove(medicineImageURL)
    }

    override fun deleteAll(medicineImageURLs: Collection<MedicineImageURL>) {
        objectStorageClient.removeAll(medicineImageURLs)
    }
}