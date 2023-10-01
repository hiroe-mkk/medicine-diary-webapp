package example.testhelper.factory

import example.application.service.profile.*
import org.springframework.http.*
import org.springframework.mock.web.*
import org.springframework.web.multipart.*
import java.io.*

object TestProfileImageFactory {
    private val defaultType: MediaType = MediaType.IMAGE_JPEG
    private val defaultSize: Int = 1024

    fun createProfileImageChangeCommand(type: MediaType = defaultType,
                                        size: Int = defaultSize): ProfileImageChangeCommand {
        return ProfileImageChangeCommand(createMultipartFile(type, size))
    }

    private fun createMultipartFile(type: MediaType = defaultType,
                                    size: Int = defaultSize): MultipartFile {
        val content = ByteArrayInputStream(ByteArray(size))
        return MockMultipartFile("profileImage", "profileImage.jpg", type.toString(), content)
    }
}