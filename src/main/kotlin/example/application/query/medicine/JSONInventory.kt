package example.application.query.medicine

import com.fasterxml.jackson.annotation.*

class JSONInventory(val remainingQuantity: Double,
                    val quantityPerPackage: Double,
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    val startedOn: String?,
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    val expirationOn: String?,
                    val unusedPackage: Int)