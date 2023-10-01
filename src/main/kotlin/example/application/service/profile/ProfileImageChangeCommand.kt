package example.application.service.profile

import example.domain.shared.type.*
import org.springframework.http.*
import org.springframework.web.multipart.*

class ProfileImageChangeCommand(private val profileImage: MultipartFile?) { // TODO: バリデーション
    fun validatedFileContent(): FileContent {
        return FileContent(MediaType.parseMediaType(checkNotNull(profileImage?.contentType)),
                           checkNotNull(profileImage?.size!!.toInt()),
                           checkNotNull(profileImage?.inputStream))
    }
}