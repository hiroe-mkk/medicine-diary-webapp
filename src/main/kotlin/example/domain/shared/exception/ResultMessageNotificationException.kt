package example.domain.shared.exception

import example.domain.shared.message.*

/**
 * ユーザーに結果メッセージを通知する必要があることを示す例外
 */
abstract class ResultMessageNotificationException(val resultMessage: ResultMessage,
                                                  cause: Throwable? = null)
    : RuntimeException(resultMessage.toString(), cause)