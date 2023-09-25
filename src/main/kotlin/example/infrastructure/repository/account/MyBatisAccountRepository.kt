package example.infrastructure.repository.account

import example.domain.model.account.*
import example.domain.shared.exception.*
import org.springframework.stereotype.*

@Repository
class MyBatisAccountRepository(private val accountMapper: AccountMapper) : AccountRepository {
    override fun findById(accountId: AccountId): Account? {
        val accountResultEntity = accountMapper.findOneAccountByAccountId(accountId.value)
        return accountResultEntity?.let { it.toAccount(findCredential(it)) }
    }

    private fun findCredential(accountResultEntity: AccountResultEntity): Credential {
        val result = when (accountResultEntity.credentialType) {
            "OAuth2" -> accountMapper.findOneOauth2CredentialByAccountId(accountResultEntity.accountId.value)
            else     -> null
        }
        return result ?: throw AssertionFailException("クレデンシャルの取得に失敗しました。")
    }

    override fun save(account: Account) {
        if (account.credential is OAuth2Credential) {
            accountMapper.saveAccount(account.id.value,
                                      account.username.value,
                                      "OAuth2")
            accountMapper.saveOauth2Credential(account.id.value,
                                               account.credential.idP,
                                               account.credential.subject)
        }
    }
}