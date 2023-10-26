package example.domain.model.sharedgroup

import example.domain.shared.exception.*

/**
 * 共有グループへの招待ができなかったことを示す例外
 */
class InvitationToSharedGroupException(details: String, cause: Throwable? = null)
    : DomainException("共有グループへの招待に失敗しました。", details, cause)