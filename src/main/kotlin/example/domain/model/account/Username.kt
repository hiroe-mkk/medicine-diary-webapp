package example.domain.model.account


/**
 * ユーザー名
 */
data class Username(val value: String) {
    companion object {
        fun defaultUsername(): Username = Username("")
    }

    override fun toString(): String = value
}