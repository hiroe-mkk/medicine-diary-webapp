package example.domain.model.sharedgroup

import example.domain.shared.exception.*

/**
 * 無効な招待であることを示す例外
 */
class InvalidInvitationException(val inviteCode: String, details: String, cause: Throwable? = null)
    : DomainException("この招待は無効です。", details, cause)
