package example.application.shared.usersession

import example.domain.model.account.*

/**
 * 認証済みユーザの情報を持つ
 */
interface UserSession {
    val accountId: AccountId
}