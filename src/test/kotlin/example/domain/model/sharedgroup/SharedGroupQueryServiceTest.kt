package example.domain.model.sharedgroup

import example.application.service.sharedgroup.*
import example.domain.model.account.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@DomainLayerTest
internal class SharedGroupQueryServiceTest(@Autowired private val sharedGroupQueryService: SharedGroupQueryService,
                                           @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                           @Autowired private val testAccountInserter: TestAccountInserter) {
    private lateinit var requesterAccountId: AccountId
    private lateinit var user1AccountId: AccountId

    @BeforeEach
    internal fun setUp() {
        requesterAccountId = testAccountInserter.insertAccountAndProfile().first.id
        user1AccountId = testAccountInserter.insertAccountAndProfile().first.id
    }

    @Nested
    inner class GetParticipatingSharedGroupTest {
        @Test
        @DisplayName("参加している共有グループを取得する")
        fun getParticipatingSharedGroup() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(requesterAccountId))

            //when:
            val actual = sharedGroupQueryService.findParticipatingSharedGroup(sharedGroup.id,
                                                                              requesterAccountId)

            //then:
            assertThat(actual).usingRecursiveComparison().isEqualTo(sharedGroup)
        }

        @Test
        @DisplayName("参加していない共有グループの場合、共有グループの取得に失敗する")
        fun notParticipatingInSharedGroup_gettingSharedGroupFails() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(user1AccountId))

            //when:
            val actual = sharedGroupQueryService.findParticipatingSharedGroup(sharedGroup.id,
                                                                              requesterAccountId)

            //then:
            assertThat(actual).isNull()
        }
    }

    @Nested
    inner class GetInvitedSharedGroupTest {
        @Test
        @DisplayName("招待されている共有グループを取得する")
        fun getInvitedSharedGroup() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(user1AccountId),
                                                             invitees = setOf(requesterAccountId))

            //when:
            val actual = sharedGroupQueryService.findInvitedSharedGroup(sharedGroup.id,
                                                                        requesterAccountId)

            //then:
            assertThat(actual).usingRecursiveComparison().isEqualTo(sharedGroup)
        }

        @Test
        @DisplayName("招待されていない共有グループの場合、共有グループの取得に失敗する")
        fun notInvitedInSharedGroup_gettingSharedGroupFails() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(user1AccountId))

            //when:
            val actual = sharedGroupQueryService.findInvitedSharedGroup(sharedGroup.id,
                                                                        requesterAccountId)

            //then:
            assertThat(actual).isNull()
        }
    }
}