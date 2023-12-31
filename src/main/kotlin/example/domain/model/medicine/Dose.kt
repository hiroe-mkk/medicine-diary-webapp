package example.domain.model.medicine

/**
 * 1回あたりの服用量
 */
data class Dose(val quantity: Double) {
    override fun toString(): String = Regex(".0+\$").replace(quantity.toString(), "")
}