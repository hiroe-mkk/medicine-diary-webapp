package example.domain.model.account.profile

import example.domain.shared.exception.*

/**
 * ユーザー名の変更に失敗したことを示す例外
 */
class UsernameChangeException(val username: Username, details: String? = null)
    : DomainException("ユーザー名の変更に失敗しました。", details)