package example.domain.model.medicine

/**
 * 用量 (1回あたりの服薬量)
 */
data class Dosage(val quantity: Double) {
    override fun toString(): String {
        return Regex(".0+\$").replace(quantity.toString(), "")
    }
}