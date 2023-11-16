package example.domain.model.medicine

import java.math.*
import java.time.*

/**
 * 在庫
 */
data class Inventory(val remainingQuantity: Double,
                     val quantityPerPackage: Double,
                     val startedOn: LocalDate?,
                     val expirationOn: LocalDate?,
                     val unusedPackage: Int) {
    fun decrease(dose: Dose): Inventory {
        val newRemainingQuantity = if (remainingQuantity <= dose.quantity) {
            0.0
        } else {
            (BigDecimal(remainingQuantity) - BigDecimal(dose.quantity)).toDouble()
        }

        return Inventory(newRemainingQuantity,
                         quantityPerPackage,
                         startedOn,
                         expirationOn,
                         unusedPackage)
    }
}