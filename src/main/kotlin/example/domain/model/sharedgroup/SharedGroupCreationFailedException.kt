package example.domain.model.sharedgroup

import example.domain.shared.exception.*

/**
 * 共有グループの作成に失敗したことを示す例外
 */
class SharedGroupCreationFailedException(details: String, cause: Throwable? = null)
    : DomainException("共有グループの作成に失敗しました。", details, cause)
