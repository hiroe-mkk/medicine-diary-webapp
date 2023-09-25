package example.domain.shared.exception

/**
 * 事前条件が満たされていないことを示す例外
 */
open class AssertionFailException(message: String, cause: Throwable? = null)
    : RuntimeException(message, cause)