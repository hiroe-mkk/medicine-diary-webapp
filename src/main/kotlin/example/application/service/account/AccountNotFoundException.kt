package example.application.service.account

import example.domain.model.account.*
import example.domain.shared.exception.*

/**
 * アカウントが見つからなかったことを示す例外
 */
class AccountNotFoundException(val accountId: AccountId)
    : ResourceNotFoundException("アカウントが見つかりませんでした。", accountId)