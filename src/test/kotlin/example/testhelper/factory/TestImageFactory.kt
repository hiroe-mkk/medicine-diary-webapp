package example.testhelper.factory

import example.application.shared.command.*
import org.springframework.http.*
import org.springframework.mock.web.*
import org.springframework.web.multipart.*
import java.io.*

object TestImageFactory {
    private val defaultType: MediaType = MediaType.IMAGE_JPEG
    private val defaultSize: Int = 1024

    fun createImageUploadCommand(type: MediaType = defaultType,
                                 size: Int = defaultSize): ImageUploadCommand {
        return ImageUploadCommand(createMultipartFile(type, size))
    }

    fun createMultipartFile(type: MediaType = defaultType,
                            size: Int = defaultSize): MultipartFile {
        val content = ByteArrayInputStream(ByteArray(size))
        return MockMultipartFile("image", "image.jpg", type.toString(), content)
    }
}