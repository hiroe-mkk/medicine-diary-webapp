package example.domain.model.sharedgroup

import example.domain.model.account.*
import example.domain.shared.type.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@DomainLayerTest
class SharedGroupFinderTest(@Autowired private val localDateTimeProvider: LocalDateTimeProvider,
                            @Autowired private val sharedGroupFinder: SharedGroupFinder,
                            @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                            @Autowired private val testAccountInserter: TestAccountInserter) {
    private lateinit var accountId: AccountId
    private val checkDate = localDateTimeProvider.today()

    @BeforeEach
    internal fun setUp() {
        accountId = testAccountInserter.insertAccountAndProfile().first.id
    }

    @Nested
    inner class GetJoinedSharedGroupTest {
        @Test
        @DisplayName("参加している共有グループを取得する")
        fun findJoinedSharedGroup() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(accountId))

            //when:
            val actual = sharedGroupFinder.findJoinedSharedGroup(accountId)

            //then:
            assertThat(actual?.id).isEqualTo(sharedGroup.id)
        }

        @Test
        @DisplayName("その共有グループのメンバーではない場合、参加している共有グループの取得に失敗する")
        fun notMemberOfSharedGroup_gettingJoinedSharedGroupFails() {
            //when:
            val actual = sharedGroupFinder.findJoinedSharedGroup(accountId)

            //then:
            assertThat(actual).isNull()
        }
    }

    @Nested
    inner class GetInvitedSharedGroupTest {
        @Test
        @DisplayName("招待されている共有グループを取得する")
        fun findInvitedSharedGroup() {
            //given:
            val pendingInvitation = SharedGroupFactory.createPendingInvitation()
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(accountId),
                                                             pendingInvitations = setOf(pendingInvitation))

            //when:
            val actual = sharedGroupFinder.findInvitedSharedGroup(pendingInvitation.inviteCode, checkDate)

            //then:
            assertThat(actual?.id).isEqualTo(sharedGroup.id)
        }

        @Test
        @DisplayName("招待されている共有グループが見つからなかった場合、参加可能な招待された共有グループIDの取得に失敗する")
        fun invitedSharedGroupNotFound_joinableInvitedSharedGroupRetrievalFails() {
            //given:
            val invalidInviteCode = "code"

            //when:
            val actual = sharedGroupFinder.findInvitedSharedGroup(invalidInviteCode, checkDate)

            //then:
            assertThat(actual).isNull()
        }

        @Test
        @DisplayName("招待コードの有効期限が過ぎている場合、招待されている共有グループの取得に失敗する")
        fun inviteCodeHasExpired_gettingInvitedSharedGroupFails() {
            //given:
            val invitedOn = checkDate.minusDays(9)
            val pendingInvitation = SharedGroupFactory.createPendingInvitation(invitedOn = invitedOn)
            testSharedGroupInserter.insert(members = setOf(accountId),
                                           pendingInvitations = setOf(pendingInvitation))

            //when:
            val actual = sharedGroupFinder.findInvitedSharedGroup(pendingInvitation.inviteCode, checkDate)

            //then:
            assertThat(actual).isNull()
        }
    }
}
