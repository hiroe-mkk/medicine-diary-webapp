package example.domain.model.medicine

/**
 * 用量 (1回あたりの服薬量)
 */
data class Dosage(val quantity: Double,
                  val takingUnit: String) {
    override fun toString(): String {
        val quantityStr = Regex(".0+\$").replace(quantity.toString(), "")
        return "1回 ${quantityStr}${takingUnit}"
    }
}