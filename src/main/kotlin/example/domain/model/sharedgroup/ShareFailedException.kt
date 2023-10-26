package example.domain.model.sharedgroup

import example.domain.shared.exception.*

/**
 * 共有に失敗したことを示す例外
 */
class ShareFailedException(details: String, cause: Throwable? = null)
    : DomainException("共有に失敗しました。", details, cause)