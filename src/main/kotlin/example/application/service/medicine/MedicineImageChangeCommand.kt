package example.application.service.medicine

import example.domain.shared.type.*
import example.domain.shared.validation.*
import org.springframework.http.*
import org.springframework.web.multipart.*

class MedicineImageChangeCommand(@field:FileNotEmpty
                                 @field:FileMaxSize(500 * 1024) // 500KB
                                 @field:FileType([MediaType.IMAGE_JPEG_VALUE])
                                 val medicineImage: MultipartFile?) {
    fun validatedFileContent(): FileContent {
        return FileContent(MediaType.parseMediaType(checkNotNull(medicineImage?.contentType)),
                           checkNotNull(medicineImage?.size!!.toInt()),
                           checkNotNull(medicineImage?.inputStream))
    }
}