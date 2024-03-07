package example.application.service.account

import example.application.shared.exception.*
import example.domain.model.account.*

/**
 * アカウントが見つからなかったことを示す例外
 */
class AccountNotFoundException(val accountId: AccountId)
    : ResourceNotFoundException("アカウントが見つかりませんでした。", accountId)