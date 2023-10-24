package example.presentation.controller.api

import com.fasterxml.jackson.annotation.*
import example.domain.shared.exception.*

/**
 * 処理実行中にエラーが発生したことを示すレスポンス
 */
class JSONErrorResponse(val error: JSONError) {
    companion object {
        fun create(message: String, details: String? = null): JSONErrorResponse {
            return JSONErrorResponse(JSONError(message, details))
        }

        fun create(exception: ResultMessageNotificationException): JSONErrorResponse {
            return JSONErrorResponse(JSONError(exception.resultMessage.message,
                                               exception.resultMessage.details))
        }
    }

    class JSONError(val message: String,
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    val details: String?)
}