package example.infrastructure.db.repository.sharedgroup

import example.domain.model.account.*
import example.domain.model.sharedgroup.*
import example.infrastructure.db.repository.shared.*
import org.springframework.stereotype.*

@Repository
class MyBatisSharedGroupRepository(private val sharedGroupMapper: SharedGroupMapper) : SharedGroupRepository {
    override fun createSharedGroupId(): SharedGroupId {
        return SharedGroupId(EntityIdHelper.generate())
    }

    override fun findById(sharedGroupId: SharedGroupId): SharedGroup? {
        return sharedGroupMapper.findOneBySharedGroupId(sharedGroupId.value)?.toSharedGroup()
    }

    override fun findByInviteCode(inviteCode: String): SharedGroup? {
        // TODO
        return null
    }

    override fun findByMember(accountId: AccountId): SharedGroup? {
        return sharedGroupMapper.findOneByMember(accountId.value)?.toSharedGroup()
    }

    override fun findByInvitee(accountId: AccountId): Set<SharedGroup> {
        return sharedGroupMapper.findAllByInvitee(accountId.value).map { it.toSharedGroup() }.toSet()
    }

    override fun save(sharedGroup: SharedGroup) { // TODO: 他の方法を考える
        sharedGroupMapper.insertSharedGroup(sharedGroup.id.value)
        upsertAllMembers(sharedGroup)
        upsertAllInvitees(sharedGroup)
    }

    private fun upsertAllMembers(sharedGroup: SharedGroup) {
        sharedGroupMapper.deleteAllMembers(sharedGroup.id.value)
        if (sharedGroup.members.isEmpty()) return

        sharedGroupMapper.insertAllMembers(sharedGroup.id.value,
                                           sharedGroup.members.map { it.value })
    }

    private fun upsertAllInvitees(sharedGroup: SharedGroup) {
        sharedGroupMapper.deleteAllInvitees(sharedGroup.id.value)
        if (sharedGroup.invitees.isEmpty()) return

        sharedGroupMapper.insertAllInvitees(sharedGroup.id.value,
                                            sharedGroup.invitees.map { it.value })
    }

    override fun deleteById(sharedGroupId: SharedGroupId) {
        sharedGroupMapper.deleteAllMembers(sharedGroupId.value)
        sharedGroupMapper.deleteAllInvitees(sharedGroupId.value)
        sharedGroupMapper.deleteSharedGroup(sharedGroupId.value)
    }
}
