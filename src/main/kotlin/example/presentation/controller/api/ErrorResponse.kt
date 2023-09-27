package example.presentation.controller.api

import com.fasterxml.jackson.annotation.*
import example.domain.shared.exception.*

/**
 * 処理実行中にエラーが発生したことを示すレスポンス
 */
class ErrorResponse(val error: Error) {
    companion object {
        fun create(message: String, details: String? = null): ErrorResponse {
            return ErrorResponse(Error(message, details))
        }

        fun create(exception: ResultMessageNotificationException): ErrorResponse {
            return ErrorResponse(Error(exception.resultMessage.message,
                                       exception.resultMessage.details))
        }
    }

    class Error(val message: String,
                @JsonInclude(JsonInclude.Include.NON_NULL)
                val details: String?)
}