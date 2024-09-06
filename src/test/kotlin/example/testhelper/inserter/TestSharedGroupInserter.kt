package example.testhelper.inserter

import example.domain.model.account.*
import example.domain.model.sharedgroup.*
import example.infrastructure.db.repository.shared.*
import org.springframework.boot.test.context.*

@TestComponent
class TestSharedGroupInserter(private val sharedGroupRepository: SharedGroupRepository) {
    /**
     * テスト用の共有グループを生成して、リポジトリに追加する
     */
    fun insert(sharedGroupId: SharedGroupId = SharedGroupId(EntityIdHelper.generate()),
               members: Set<AccountId> = emptySet(),
               pendingInvitations: Set<PendingInvitation> = emptySet()): SharedGroup {
        val sharedGroup = SharedGroup(sharedGroupId, members, pendingInvitations)
        sharedGroupRepository.save(sharedGroup)
        return sharedGroup
    }
}
