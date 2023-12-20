package example.domain.shared.type

/**
 * メールアドレス
 */
data class EmailAddress(val value: String) {
    override fun toString(): String = value
}