package example.application.service.profile

import example.domain.model.account.*
import example.domain.shared.exception.*

/**
 * プロフィールが見つからなかったことを示す例外
 */
class ProfileNotFoundException(val accountId: AccountId)
    : ResourceNotFoundException("プロフィールが見つかりませんでした。", accountId)