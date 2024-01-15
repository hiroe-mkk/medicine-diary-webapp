package example.application.query.sharedgroup

import example.application.query.user.*

class JSONSharedGroup(val sharedGroupId: String) {
    private lateinit var membersForMapping: Set<JSONUser>
    val members: Set<JSONUser>
        get() = membersForMapping

    private lateinit var inviteesForMapping: Set<JSONUser>
    val invitees: Set<JSONUser>
        get() = inviteesForMapping
}