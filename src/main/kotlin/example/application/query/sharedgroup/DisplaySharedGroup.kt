package example.application.query.sharedgroup

import example.application.query.shared.type.*
import example.domain.model.sharedgroup.*

data class DisplaySharedGroup(val sharedGroupId: SharedGroupId) {
    val members: Set<User> = mutableSetOf()
    val invitees: Set<User> = mutableSetOf()
}