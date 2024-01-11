package example.presentation.shared.springframework.security

import example.domain.model.account.*

/**
 * 認証済みアカウント
 */
interface AuthenticatedAccount {
    val id: AccountId
}