package example.infrastructure.query.sharedgroup

import example.application.query.shared.type.*
import example.application.query.sharedgroup.*
import example.domain.model.sharedgroup.*

class SharedGroupDetailResultEntity(val sharedGroupId: SharedGroupId) {
    val members: Set<User> = mutableSetOf()
    val invitees: Set<User> = mutableSetOf()

    fun toSharedGroupDetail(): SharedGroupDetail {
        return SharedGroupDetail(sharedGroupId, members, invitees)
    }
}