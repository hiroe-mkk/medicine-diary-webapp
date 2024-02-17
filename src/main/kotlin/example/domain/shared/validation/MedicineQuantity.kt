package example.domain.shared.validation

import jakarta.validation.*
import jakarta.validation.constraints.*
import kotlin.reflect.*

@Constraint(validatedBy = [])
@Target(AnnotationTarget.FIELD)
@Digits(integer = 4, fraction = 3)
@DecimalMin(value = "0.001")
annotation class MedicineQuantity(val message: String = "※0.001以上、10000未満の範囲の数値を入力してください。",
                                  val groups: Array<KClass<out Any>> = [],
                                  val payload: Array<KClass<out Payload>> = [])