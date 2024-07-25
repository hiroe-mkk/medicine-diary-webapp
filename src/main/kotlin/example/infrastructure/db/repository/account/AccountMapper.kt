package example.infrastructure.db.repository.account

import example.domain.model.account.*
import org.apache.ibatis.annotations.*

@Mapper
interface AccountMapper {
    fun findOneAccountByAccountId(accountId: String): AccountResultEntity?

    fun findOneOauth2CredentialByAccountId(accountId: String): OAuth2Credential?

    fun findOneAccountIdByOAuth2Credential(idP: IdP, subject: String): AccountId?

    fun insertAccount(accountId: String, credentialType: String)

    fun insertOauth2Credential(accountId: String,
                               idP: IdP, subject: String)

    fun deleteAccount(accountId: String)

    fun deleteOauth2Credential(accountId: String)
}
