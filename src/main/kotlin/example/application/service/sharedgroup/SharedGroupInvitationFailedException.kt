package example.application.service.sharedgroup

import example.domain.shared.exception.*

/**
 * 共有グループ招待に失敗したことを示す例外
 */
class SharedGroupInvitationFailedException(details: String, cause: Throwable? = null)
    : DomainException("共有グループへの招待に失敗しました。", details, cause)