package example.application.shared.exception

import example.domain.shared.exception.*
import example.domain.shared.message.*

/**
 * アプリケーションサービス層で問題が発生したことを示す例外クラス
 */
abstract class ApplicationServiceException(message: String, details: String? = null, cause: Throwable? = null)
    : ResultMessageNotificationException(ResultMessage.error(message, details), cause)
