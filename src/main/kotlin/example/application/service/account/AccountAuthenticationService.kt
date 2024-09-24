package example.application.service.account

import example.domain.model.account.*
import example.domain.model.account.profile.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class AccountAuthenticationService(private val accountRepository: AccountRepository,
                                   private val profileRepository: ProfileRepository) {
    /**
     * アカウントを取得または作成する
     *
     * 指定されたクレデンシャルを持つアカウントを返すか、見つからなかった場合は作成した結果を返す
     */
    fun findOrElseCreateAccount(credential: Credential): Account {
        return accountRepository.findByCredential(credential) ?: createAccount(credential)
    }

    private fun createAccount(credential: Credential): Account {
        val accountId = accountRepository.createAccountId()
        val (account, profile) = Account.create(accountId, credential)

        accountRepository.save(account)
        profileRepository.save(profile)

        return account
    }
}
