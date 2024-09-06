package example.infrastructure.db.repository.sharedgroup

import example.domain.model.account.*
import example.domain.model.sharedgroup.*
import example.infrastructure.db.repository.shared.*
import org.apache.commons.lang3.*
import org.springframework.stereotype.*

@Repository
class MyBatisSharedGroupRepository(private val sharedGroupMapper: SharedGroupMapper) : SharedGroupRepository {
    override fun createSharedGroupId(): SharedGroupId {
        return SharedGroupId(EntityIdHelper.generate())
    }

    override fun createInviteCode(): String {
        var inviteCode: String
        do {
            inviteCode = RandomStringUtils.randomAlphanumeric(8).uppercase()
        } while (sharedGroupMapper.findOneByInviteCode(inviteCode) != null)

        return inviteCode
    }

    override fun findById(sharedGroupId: SharedGroupId): SharedGroup? {
        return sharedGroupMapper.findOneBySharedGroupId(sharedGroupId.value)?.toSharedGroup()
    }

    override fun findByInviteCode(inviteCode: String): SharedGroup? {
        return sharedGroupMapper.findOneByInviteCode(inviteCode)?.toSharedGroup();
    }

    override fun findByMember(accountId: AccountId): SharedGroup? {
        return sharedGroupMapper.findOneByMember(accountId.value)?.toSharedGroup()
    }

    override fun save(sharedGroup: SharedGroup) { // TODO: 他の方法を考える
        sharedGroupMapper.insertSharedGroup(sharedGroup.id.value)
        upsertAllMembers(sharedGroup)
        upsertAllPendingInvitations(sharedGroup)
    }

    private fun upsertAllMembers(sharedGroup: SharedGroup) {
        sharedGroupMapper.deleteAllMembers(sharedGroup.id.value)
        if (sharedGroup.members.isEmpty()) return

        sharedGroupMapper.insertAllMembers(sharedGroup.id.value,
                                           sharedGroup.members.map { it.value })
    }

    private fun upsertAllPendingInvitations(sharedGroup: SharedGroup) {
        sharedGroupMapper.deleteAllPendingInvitations(sharedGroup.id.value)
        if (sharedGroup.pendingInvitations.isEmpty()) return

        sharedGroupMapper.insertAllPendingInvitations(sharedGroup.id.value,
                                                      sharedGroup.pendingInvitations)
    }

    override fun deleteById(sharedGroupId: SharedGroupId) {
        sharedGroupMapper.deleteAllMembers(sharedGroupId.value)
        sharedGroupMapper.deleteAllPendingInvitations(sharedGroupId.value)
        sharedGroupMapper.deleteSharedGroup(sharedGroupId.value)
    }
}
