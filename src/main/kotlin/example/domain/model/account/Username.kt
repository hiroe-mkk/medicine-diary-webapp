package example.domain.model.account


/**
 * ユーザー名
 */
data class Username(val value: String) {
    override fun toString(): String = value
}