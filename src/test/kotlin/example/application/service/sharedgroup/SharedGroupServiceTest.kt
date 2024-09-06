package example.application.service.sharedgroup

import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.sharedgroup.*
import example.domain.shared.type.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@DomainLayerTest
internal class SharedGroupServiceTest(@Autowired private val sharedGroupRepository: SharedGroupRepository,
                                      @Autowired private val localDateTimeProvider: LocalDateTimeProvider,
                                      @Autowired private val sharedGroupLeaveService: SharedGroupLeaveService,
                                      @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                      @Autowired private val testAccountInserter: TestAccountInserter) {
    private val sharedGroupService: SharedGroupService = SharedGroupService(sharedGroupRepository,
                                                                            localDateTimeProvider,
                                                                            sharedGroupLeaveService)

    private lateinit var userSession: UserSession
    private lateinit var user1AccountId: AccountId

    @BeforeEach
    internal fun setUp() {
        val requesterAccountId = testAccountInserter.insertAccountAndProfile().first.id
        userSession = UserSessionFactory.create(requesterAccountId)
        user1AccountId = createAccount().id
    }

    @Nested
    inner class RequestToShareTest {
        @Test
        @DisplayName("共有グループを作る")
        fun createShredGroup() {
            //when:
            val sharedGroupId = sharedGroupService.createSharedGroup(userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroupId)
            assertThat(foundSharedGroup?.members).containsExactlyInAnyOrder(userSession.accountId)
        }

        @Test
        @DisplayName("既に共有グループに参加している場合、共有グループの作成に失敗する")
        fun joinedShredGroup_sharedGroupCreationFails() {
            //given:
            testSharedGroupInserter.insert(members = setOf(userSession.accountId))

            //when:
            val target: () -> Unit = { sharedGroupService.createSharedGroup(userSession) }

            //then:
            assertThrows<SharedGroupCreationFailedException>(target)
        }
    }

    @Nested
    inner class InviteToSharedGroupTest {
        @Test
        @DisplayName("共有グループに招待する")
        fun inviteToSharedGroup() {
            //given:
            val members = setOf(userSession.accountId)
            val sharedGroup = testSharedGroupInserter.insert(members = members)

            //when:
            sharedGroupService.inviteToSharedGroup(sharedGroup.id, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
            assertThat(foundSharedGroup?.members).containsExactlyInAnyOrder(*members.toTypedArray())
        }

        @Test
        @DisplayName("参加している共有グループが見つからなかった場合、共有グループへの招待に失敗する")
        fun joinedSharedGroupNotFound_sharedGroupInviteFails() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(createAccount().id))

            //when:
            val target: () -> Unit = {
                sharedGroupService.inviteToSharedGroup(sharedGroup.id, userSession)
            }

            //then:
            assertThrows<SharedGroupInviteFailedException>(target)
        }
    }

    @Nested
    inner class JoinSharedGroupTest {
        @Test
        @DisplayName("共有グループに参加する")
        fun joinSharedGroup() {
            //given:
            val pendingInvitation = SharedGroupFactory.createPendingInvitation()
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(user1AccountId),
                                                             pendingInvitations = setOf(pendingInvitation))

            //when:
            sharedGroupService.joinSharedGroup(pendingInvitation.inviteCode, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
            assertThat(foundSharedGroup?.members).containsExactlyInAnyOrder(user1AccountId, userSession.accountId)
            assertThat(foundSharedGroup?.pendingInvitations).isEmpty()
        }

        @Test
        @DisplayName("既に共有グループに参加している場合、共有グループへの参加に失敗する")
        fun alreadyJoinedShredGroup_JoinedSharedGroupFails() {
            //given:
            testSharedGroupInserter.insert(members = setOf(userSession.accountId))
            val pendingInvitation = SharedGroupFactory.createPendingInvitation()
            testSharedGroupInserter.insert(members = setOf(createAccount().id),
                                           pendingInvitations = setOf(pendingInvitation))

            //when:
            val target: () -> Unit = { sharedGroupService.joinSharedGroup(pendingInvitation.inviteCode, userSession) }

            //then:
            assertThrows<SharedGroupJoinFailedException>(target)
        }

        @Test
        @DisplayName("招待コードの有効期限が過ぎている場合、共有グループへの参加に失敗する")
        fun inviteCodeHasExpired_JoinedSharedGroupFails() {
            //given:
            val pendingInvitation =
                    SharedGroupFactory.createPendingInvitation(invitedOn = localDateTimeProvider.today().minusDays(9))
            testSharedGroupInserter.insert(members = setOf(createAccount().id),
                                           pendingInvitations = setOf(pendingInvitation))

            //when:
            val target: () -> Unit = { sharedGroupService.joinSharedGroup(pendingInvitation.inviteCode, userSession) }

            //then:
            assertThrows<SharedGroupJoinFailedException>(target)
        }

        @Test
        @DisplayName("招待されている共有グループが見つからなかった場合、共有グループへの参加に失敗する")
        fun invitedSharedGroupNotFound_JoinedSharedGroupFails() {
            //when:
            val target: () -> Unit = { sharedGroupService.joinSharedGroup("", userSession) }

            //then:
            assertThrows<SharedGroupJoinFailedException>(target)
        }
    }

    @Nested
    inner class RejectInvitationToSharedGroupTest {
        @Test
        @DisplayName("共有グループへの招待を拒否する")
        fun rejectInvitation() {
            //given:
            val pendingInvitation = SharedGroupFactory.createPendingInvitation()
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(user1AccountId),
                                                             pendingInvitations = setOf(pendingInvitation))

            //when:
            sharedGroupService.rejectInvitationToSharedGroup(pendingInvitation.inviteCode, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
            assertThat(foundSharedGroup?.pendingInvitations).isEmpty()
        }
    }

    private fun createAccount() = testAccountInserter.insertAccountAndProfile().first
}
