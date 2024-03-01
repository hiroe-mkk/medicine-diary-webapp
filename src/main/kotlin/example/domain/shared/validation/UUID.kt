package example.domain.shared.validation

import jakarta.validation.*
import kotlin.reflect.*

/**
 * UUID 形式の文字列であることを保証するアノテーション
 */
@Target(AnnotationTarget.FIELD)
@Constraint(validatedBy = [UUID.Validator::class])
annotation class UUID(val message: String = "※不正な形式です。",
                      val groups: Array<KClass<out Any>> = [],
                      val payload: Array<KClass<out Payload>> = []) {
    class Validator : ConstraintValidator<NotWhitespaceOnly, String> {
        override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
            if (value == null) return true

            return try {
                java.util.UUID.fromString(value)
                true
            } catch (ex: IllegalArgumentException) {
                false
            }
        }
    }
}