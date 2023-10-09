package example.application.shared.command

import example.domain.shared.type.*
import example.domain.shared.validation.*
import org.springframework.http.*
import org.springframework.web.multipart.*

class ImageChangeCommand(@field:FileNotEmpty
                                @field:FileMaxSize(300 * 1024) // 300KB
                                @field:FileType([MediaType.IMAGE_JPEG_VALUE])
                                val image: MultipartFile?) {
    fun validatedFileContent(): FileContent {
        return FileContent(MediaType.parseMediaType(checkNotNull(image?.contentType)),
                           checkNotNull(image?.size!!.toInt()),
                           checkNotNull(image?.inputStream))
    }
}