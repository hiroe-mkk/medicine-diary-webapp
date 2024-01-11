package example.domain.shared.validation

import jakarta.validation.*
import org.springframework.web.multipart.*
import kotlin.reflect.*

/**
 * MultipartFile の ContentType が指定された MIME タイプの一覧に含まれることを保証するアノテーション
 */
@Target(AnnotationTarget.FIELD)
@Constraint(validatedBy = [FileType.Validator::class])
annotation class FileType(val value: Array<String>,
                          val message: String = "※アップロードできない形式のファイルが指定されています。",
                          val groups: Array<KClass<out Any>> = [],
                          val payload: Array<KClass<out Payload>> = []) {
    class Validator : ConstraintValidator<FileType, MultipartFile> {
        private var value: Array<String> = emptyArray()

        override fun initialize(constraintAnnotation: FileType) {
            this.value = constraintAnnotation.value
        }

        override fun isValid(multipartFile: MultipartFile?, context: ConstraintValidatorContext): Boolean {
            if (multipartFile == null) return true
            return value.contains(multipartFile.contentType)
        }
    }
}