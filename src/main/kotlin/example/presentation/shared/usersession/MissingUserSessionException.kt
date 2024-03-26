package example.presentation.shared.usersession

import example.domain.shared.exception.*

/**
 * 認証済みユーザのみを扱う箇所でユーザセッションが取得できなかったことを示す例外
 */
class MissingUserSessionException : AssertionFailException("Failed to retrieve UserSession in authenticated context.")