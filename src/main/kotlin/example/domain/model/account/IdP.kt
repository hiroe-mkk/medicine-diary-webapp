package example.domain.model.account

/**
 * IdP
 */
enum class IdP(val registrationId: String) {
    GITHUB("github"),
    LINE("line"),
    YAHOO("yahoo");

    companion object {
        private val stringToEnum: Map<String, IdP> = values().associateBy { it.registrationId }

        fun from(registrationId: String): IdP {
            return stringToEnum[registrationId] ?: throw IllegalArgumentException("一致するIdPがありません。")
        }
    }
}