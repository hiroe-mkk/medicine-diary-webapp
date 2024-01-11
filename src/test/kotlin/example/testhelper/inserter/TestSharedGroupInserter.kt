package example.testhelper.inserter

import example.domain.model.account.*
import example.domain.model.sharedgroup.*
import org.springframework.boot.test.context.*

@TestComponent
class TestSharedGroupInserter(private val sharedGroupRepository: SharedGroupRepository) {
    private var num: Int = 1

    /**
     * テスト用の共有グループを生成して、リポジトリに追加する
     */
    fun insert(sharedGroupId: SharedGroupId = SharedGroupId("testSharedGroupId${num++}"),
               members: Set<AccountId> = emptySet(),
               invitees: Set<AccountId> = emptySet()): SharedGroup {
        val sharedGroup = SharedGroup(sharedGroupId, members, invitees)
        sharedGroupRepository.save(sharedGroup)
        return sharedGroup
    }
}