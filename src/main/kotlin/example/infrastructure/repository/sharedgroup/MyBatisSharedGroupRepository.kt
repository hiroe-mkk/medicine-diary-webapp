package example.infrastructure.repository.sharedgroup

import example.domain.model.sharedgroup.*
import org.springframework.stereotype.*

@Repository
class MyBatisSharedGroupRepository(private val sharedGroupMapper: SharedGroupMapper) : SharedGroupRepository {
    override fun findById(sharedGroupId: SharedGroupId): SharedGroup? {
        return sharedGroupMapper.findOneBySharedGroupId(sharedGroupId.value)?.toSharedGroup()
    }

    override fun save(sharedGroup: SharedGroup) {
        upsertOneSharedGroup(sharedGroup)
        upsertAllMembers(sharedGroup)
        upsertAllPendingUsers(sharedGroup)
    }

    private fun upsertOneSharedGroup(sharedGroup: SharedGroup) {
        if (findById(sharedGroup.id) == null) {
            sharedGroupMapper.insertOneSharedGroup(sharedGroup.id.value)
        }
    }

    private fun upsertAllMembers(sharedGroup: SharedGroup) {
        sharedGroupMapper.deleteAllMembers(sharedGroup.id.value)
        if (sharedGroup.members.isEmpty()) return

        sharedGroupMapper.insertAllMembers(sharedGroup.id.value,
                                           sharedGroup.members.map { it.value })
    }

    private fun upsertAllPendingUsers(sharedGroup: SharedGroup) {
        sharedGroupMapper.deleteAllPendingUsers(sharedGroup.id.value)
        if (sharedGroup.pendingUsers.isEmpty()) return

        sharedGroupMapper.insertAllPendingUsers(sharedGroup.id.value,
                                                sharedGroup.pendingUsers.map { it.value })
    }

    override fun delete(sharedGroupId: SharedGroupId) {
        sharedGroupMapper.deleteAllMembers(sharedGroupId.value)
        sharedGroupMapper.deleteAllPendingUsers(sharedGroupId.value)
        sharedGroupMapper.deleteOneSharedGroup(sharedGroupId.value)
    }
}