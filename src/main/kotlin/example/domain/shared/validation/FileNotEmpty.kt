package example.domain.shared.validation

import jakarta.validation.*
import org.springframework.web.multipart.*
import kotlin.reflect.*

/**
 * MultipartFile が null または空ではないことを保証するアノテーション
 */
@Target(AnnotationTarget.FIELD)
@Constraint(validatedBy = [FileNotEmpty.Validator::class])
annotation class FileNotEmpty(val message: String = "ファイルを選択してください。",
                              val groups: Array<KClass<out Any>> = [],
                              val payload: Array<KClass<out Payload>> = []) {
    class Validator : ConstraintValidator<FileNotEmpty, MultipartFile> {
        override fun isValid(multipartFile: MultipartFile?, context: ConstraintValidatorContext): Boolean {
            return !(multipartFile == null || multipartFile.isEmpty)
        }
    }
}