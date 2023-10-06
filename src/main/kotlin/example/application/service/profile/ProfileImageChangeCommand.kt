package example.application.service.profile

import example.domain.shared.type.*
import example.domain.shared.validation.*
import org.springframework.http.*
import org.springframework.web.multipart.*

class ProfileImageChangeCommand(@field:FileNotEmpty
                                @field:FileMaxSize(300 * 1024) // 300KB
                                @field:FileType([MediaType.IMAGE_JPEG_VALUE])
                                val profileImage: MultipartFile?) {
    fun validatedFileContent(): FileContent {
        return FileContent(MediaType.parseMediaType(checkNotNull(profileImage?.contentType)),
                           checkNotNull(profileImage?.size!!.toInt()),
                           checkNotNull(profileImage?.inputStream))
    }
}