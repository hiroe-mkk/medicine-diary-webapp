package example.domain.model.account.profile

/**
 * ユーザー名
 */
data class Username(val value: String) {
    companion object {
        fun creteDefaultUsername(): Username = Username("")
    }

    fun isEmpty(): Boolean = value.isBlank()

    override fun toString(): String = value
}