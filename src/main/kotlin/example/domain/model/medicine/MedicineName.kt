package example.domain.model.medicine

/**
 * 薬名
 */
data class MedicineName(val value: String) {
    override fun toString(): String = value
}