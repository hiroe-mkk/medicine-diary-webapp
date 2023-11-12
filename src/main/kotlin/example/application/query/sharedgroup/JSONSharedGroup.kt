package example.application.query.sharedgroup

import example.application.query.shared.type.*
import example.application.query.user.*
import example.domain.model.sharedgroup.*

data class JSONSharedGroup(val sharedGroupId: String) {
    val members: Set<JSONUser> = mutableSetOf()
    val invitees: Set<JSONUser> = mutableSetOf()
}