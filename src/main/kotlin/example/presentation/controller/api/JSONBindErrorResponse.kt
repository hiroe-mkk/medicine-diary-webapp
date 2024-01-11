package example.presentation.controller.api

/**
 * バインドエラーが発生したことを示すレスポンス
 */
class JSONBindErrorResponse(val fieldErrors: Map<String, List<String>>)