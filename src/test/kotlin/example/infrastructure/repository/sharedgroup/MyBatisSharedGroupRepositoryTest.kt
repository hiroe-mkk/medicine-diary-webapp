package example.infrastructure.repository.sharedgroup

import example.domain.model.account.*
import example.domain.model.sharedgroup.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@MyBatisRepositoryTest
internal class MyBatisSharedGroupRepositoryTest(@Autowired private val sharedGroupRepository: SharedGroupRepository,
                                                @Autowired private val testAccountInserter: TestAccountInserter) {
    private lateinit var accountId: AccountId

    @BeforeEach
    internal fun setUp() {
        val (account, _) = testAccountInserter.insertAccountAndProfile()
        accountId = account.id
    }

    @Test
    fun afterSavingSharedGroup_canFindById() {
        //given:
        val accounts = List(3) { testAccountInserter.insertAccountAndProfile().first }
        val sharedGroup = SharedGroup(SharedGroupId("testSharedGroupId"),
                                      setOf(accounts[0].id, accounts[1].id, accounts[2].id),
                                      emptySet())

        //when:
        sharedGroupRepository.save(sharedGroup)
        val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)

        //then:
        assertThat(foundSharedGroup?.id).isEqualTo(sharedGroup.id)
        assertThat(foundSharedGroup?.members).containsExactlyInAnyOrder(*(sharedGroup.members.toTypedArray()))
        assertThat(foundSharedGroup?.pendingUsers).containsExactlyInAnyOrder(*(sharedGroup.pendingUsers.toTypedArray()))
    }
}