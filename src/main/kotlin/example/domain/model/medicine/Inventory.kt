package example.domain.model.medicine

import java.time.*

/**
 * 在庫
 */
data class Inventory(val remainingQuantity: Double,
                     val quantityPerPackage: Double,
                     val startOn: LocalDate?,
                     val expirationOn: LocalDate?,
                     val unusedPackage: Int)