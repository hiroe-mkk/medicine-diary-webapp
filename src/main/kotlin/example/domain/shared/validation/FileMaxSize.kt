package example.domain.shared.validation

import jakarta.validation.*
import org.springframework.web.multipart.*
import kotlin.reflect.*

/**
 * MultipartFile の ContentSize が指定されたサイズを超えていないことを保証するアノテーション
 */
@Target(AnnotationTarget.FIELD)
@Constraint(validatedBy = [FileMaxSize.Validator::class])
annotation class FileMaxSize(val value: Int,
                             val message: String = "上限サイズを超過しています。",
                             val groups: Array<KClass<out Any>> = [],
                             val payload: Array<KClass<out Payload>> = []) {
    class Validator : ConstraintValidator<FileMaxSize, MultipartFile> {
        private var value: Int = 0

        override fun initialize(constraintAnnotation: FileMaxSize) {
            this.value = constraintAnnotation.value
        }

        override fun isValid(multipartFile: MultipartFile?, context: ConstraintValidatorContext): Boolean {
            if (multipartFile == null || multipartFile.isEmpty) return true
            return multipartFile.size <= value
        }
    }
}