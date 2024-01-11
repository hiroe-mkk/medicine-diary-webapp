package example.domain.model.medicine

/**
 * 効能
 */
data class Effects(val values: List<String>) {
    fun isEmpty(): Boolean = values.isEmpty()

    override fun toString(): String {
        return values.joinToString(transform = { it },
                                   separator = "、",
                                   prefix = "(", postfix = ")")
    }
}