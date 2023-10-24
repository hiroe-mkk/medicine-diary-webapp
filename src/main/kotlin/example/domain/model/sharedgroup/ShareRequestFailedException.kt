package example.domain.model.sharedgroup

import example.domain.shared.exception.*

/**
 * 共有リクエストに失敗したことを示す例外
 */
class ShareRequestFailedException(details: String, cause: Throwable? = null)
    : DomainException("共有のリクエストに失敗しました。", details, cause)