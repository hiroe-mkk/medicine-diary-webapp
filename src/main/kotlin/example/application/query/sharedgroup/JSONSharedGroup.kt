package example.application.query.sharedgroup

import example.application.query.user.*

data class JSONSharedGroup(val sharedGroupId: String) {
    val members: Set<JSONUser> = mutableSetOf()
    val invitees: Set<JSONUser> = mutableSetOf()
}