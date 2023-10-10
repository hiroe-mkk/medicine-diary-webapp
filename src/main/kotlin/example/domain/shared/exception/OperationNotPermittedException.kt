package example.domain.shared.exception

/**
 * 許可されていない操作のリクエストであることを示す例外
 */
class OperationNotPermittedException(message: String, details: String? = null, cause: Throwable? = null)
    : DomainException(message, details, cause)