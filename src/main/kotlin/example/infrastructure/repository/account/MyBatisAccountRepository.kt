package example.infrastructure.repository.account

import example.domain.model.account.*
import example.domain.shared.exception.*
import example.infrastructure.repository.shared.*
import org.springframework.stereotype.*

@Repository
class MyBatisAccountRepository(private val accountMapper: AccountMapper) : AccountRepository {
    override fun createAccountId(): AccountId {
        return AccountId(EntityIdHelper.generate())
    }

    override fun isValidAccountId(accountId: AccountId): Boolean {
        return EntityIdHelper.isValid(accountId)
    }

    override fun findById(accountId: AccountId): Account? {
        val accountResultEntity = accountMapper.findOneAccountByAccountId(accountId.value) ?: return null
        return accountResultEntity.toAccount(findCredential(accountResultEntity))
    }

    private fun findCredential(accountResultEntity: AccountResultEntity): Credential {
        val result = when (accountResultEntity.credentialType) {
            "OAuth2" -> accountMapper.findOneOauth2CredentialByAccountId(accountResultEntity.accountId.value)
            else     -> null
        }
        return result ?: throw AssertionFailException("Unsupported credentialType.")
    }

    override fun findByCredential(credential: Credential): Account? {
        val accountId = findAccountId(credential) ?: return null
        val accountResultEntity = accountMapper.findOneAccountByAccountId(accountId.value)
        return accountResultEntity?.toAccount(credential)
    }

    private fun findAccountId(credential: Credential): AccountId? {
        return when (credential) {
            is OAuth2Credential -> accountMapper.findOneAccountIdByOAuth2Credential(credential.idP,
                                                                                    credential.subject)
            else                -> null
        }
    }

    override fun save(account: Account) {
        if (account.credential is OAuth2Credential) {
            accountMapper.insertAccount(account.id.value,
                                        "OAuth2")
            accountMapper.insertOauth2Credential(account.id.value,
                                                 account.credential.idP,
                                                 account.credential.subject)
        }
    }

    override fun deleteById(accountId: AccountId) {
        val account = findById(accountId) ?: return

        if (account.credential is OAuth2Credential) {
            accountMapper.deleteOauth2Credential(account.id.value)
        }
        accountMapper.deleteAccount(account.id.value)
    }
}