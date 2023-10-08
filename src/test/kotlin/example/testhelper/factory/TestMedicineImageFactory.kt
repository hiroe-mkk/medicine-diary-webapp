package example.testhelper.factory

import example.application.service.medicine.*
import org.springframework.http.*
import org.springframework.mock.web.*
import org.springframework.web.multipart.*
import java.io.*

object TestMedicineImageFactory {
    private val defaultType: MediaType = MediaType.IMAGE_JPEG
    private val defaultSize: Int = 1024

    fun createMedicineImageChangeCommand(type: MediaType = defaultType,
                                         size: Int = defaultSize): MedicineImageChangeCommand {
        return MedicineImageChangeCommand(createMultipartFile(type, size))
    }

    fun createMultipartFile(type: MediaType = defaultType,
                            size: Int = defaultSize): MultipartFile {
        val content = ByteArrayInputStream(ByteArray(size))
        return MockMultipartFile("image", "medicineImage.jpg", type.toString(), content)
    }
}