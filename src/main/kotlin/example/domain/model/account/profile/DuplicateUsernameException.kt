package example.domain.model.account.profile

import example.domain.shared.exception.*

/**
 * ユーザー名が重複していることを示す例外
 */
class DuplicateUsernameException(val username: Username)
    : DomainException("既に登録されているユーザー名です。")