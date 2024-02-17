package example.domain.shared.validation

import jakarta.validation.*
import kotlin.reflect.*

/**
 * 空白のみではないことを保証するアノテーション
 */
@Target(AnnotationTarget.FIELD)
@Constraint(validatedBy = [NotWhitespaceOnly.Validator::class])
annotation class NotWhitespaceOnly(val message: String = "※必ず入力してください。",
                                   val groups: Array<KClass<out Any>> = [],
                                   val payload: Array<KClass<out Payload>> = []) {
    class Validator : ConstraintValidator<NotWhitespaceOnly, String> {
        override fun isValid(text: String?, context: ConstraintValidatorContext): Boolean {
            return text?.isNotBlank() ?: true
        }
    }
}