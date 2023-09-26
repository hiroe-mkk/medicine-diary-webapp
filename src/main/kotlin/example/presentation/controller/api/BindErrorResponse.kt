package example.presentation.controller.api

/**
 * バインドエラーが発生したことを示すレスポンス
 */
data class BindErrorResponse(val fieldErrors: Map<String, List<String>>)