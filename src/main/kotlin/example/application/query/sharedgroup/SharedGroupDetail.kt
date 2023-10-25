package example.application.query.sharedgroup

import example.application.query.shared.type.*
import example.domain.model.sharedgroup.*

data class SharedGroupDetail(val sharedGroupId: SharedGroupId,
                             val members: Set<User>,
                             val invitees: Set<User>)