package example.application.service.account

import example.domain.model.account.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional(readOnly = true)
class AccountQueryService(private val accountRepository: AccountRepository) {
    /**
     * 有効なアカウント ID か
     */
    fun isValidAccountId(accountId: AccountId): Boolean {
        return accountRepository.isValidAccountId(accountId)
    }
}
