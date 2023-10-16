package example.domain.model.sharedgroup

import example.domain.model.account.*

/**
 * 共有グループ
 */
class SharedGroup(val id: SharedGroupId,
                  val members: Set<AccountId>,
                  val pendingUsers: Set<AccountId>)