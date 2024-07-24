package example.domain.shared.exception

import example.domain.shared.message.*

/**
 * ビジネスルールの違反を検知したことを示す例外クラス
 */
abstract class DomainException(message: String, details: String? = null, cause: Throwable? = null)
    : ResultMessageNotificationException(ResultMessage.error(message, details), cause)
