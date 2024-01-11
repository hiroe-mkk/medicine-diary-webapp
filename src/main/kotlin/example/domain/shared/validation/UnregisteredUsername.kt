package example.domain.shared.validation

import example.domain.model.account.profile.*
import example.presentation.shared.usersession.*
import jakarta.validation.*
import org.springframework.stereotype.*
import org.springframework.web.multipart.*
import kotlin.reflect.*

/**
 * ユーザー名が登録済みでないことを保証するアノテーション
 */
@Target(AnnotationTarget.FIELD)
@Constraint(validatedBy = [UnregisteredUsernameValidator::class])
annotation class UnregisteredUsername(val message: String = "※既に登録されているユーザー名です。",
                                      val groups: Array<KClass<out Any>> = [],
                                      val payload: Array<KClass<out Payload>> = [])