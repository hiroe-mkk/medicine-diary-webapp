package example.domain.shared.validation

import jakarta.validation.*
import kotlin.reflect.*

/**
 * ユーザー名が登録済みでないことを保証するアノテーション
 */
@Target(AnnotationTarget.FIELD)
@Constraint(validatedBy = [UnregisteredUsernameValidator::class])
annotation class UnregisteredUsername(val message: String = "※既に登録されているユーザー名です。",
                                      val groups: Array<KClass<out Any>> = [],
                                      val payload: Array<KClass<out Payload>> = [])