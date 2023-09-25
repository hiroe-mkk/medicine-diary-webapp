package example.infrastructure.repository.account

import example.domain.model.account.*
import org.apache.ibatis.annotations.*

@Mapper
interface AccountMapper {
    fun findOneAccountByAccountId(accountId: String): AccountResultEntity?

    fun findOneOauth2CredentialByAccountId(accountId: String): OAuth2Credential?

    fun saveAccount(accountId: String,
                    username: String,
                    credentialType: String)

    fun saveOauth2Credential(accountId: String,
                             idP: IdP,
                             subject: String)
}