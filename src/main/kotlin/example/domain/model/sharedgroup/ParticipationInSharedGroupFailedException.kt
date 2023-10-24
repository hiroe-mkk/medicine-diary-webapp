package example.domain.model.sharedgroup

import example.domain.shared.exception.*

/**
 * 共有グループへの参加に失敗したことを示す例外
 */
class ParticipationInSharedGroupFailedException(details: String, cause: Throwable? = null)
    : DomainException("共有グループへの参加に失敗しました。", details, cause)