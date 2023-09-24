package example.domain.model.account

/**
 * OAuth 2.0 プロバイダーに登録されているユーザーのクレデンシャル
 */
class OAuth2Credential(val idP: IdP,
                       val subject: String) : Credential {
    override fun toString(): String = "$subject (IdP: ${idP})"
}