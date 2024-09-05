package example.domain.model.sharedgroup

import example.domain.shared.exception.*

/**
 * 共有グループへの参加ができなかったことを示す例外
 */
class ParticipationInSharedGroupException(details: String, cause: Throwable? = null)
    : DomainException("共有グループへの参加に失敗しました。", details, cause)
