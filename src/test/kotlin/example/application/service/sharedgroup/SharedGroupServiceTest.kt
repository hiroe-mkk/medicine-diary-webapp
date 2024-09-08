package example.application.service.sharedgroup

import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.sharedgroup.*
import example.domain.shared.type.*
import example.infrastructure.email.shared.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import io.mockk.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@DomainLayerTest
internal class SharedGroupServiceTest(@Autowired private val sharedGroupRepository: SharedGroupRepository,
                                      @Autowired private val localDateTimeProvider: LocalDateTimeProvider,
                                      @Autowired private val emailSenderClient: EmailSenderClient,
                                      @Autowired private val sharedGroupLeaveService: SharedGroupLeaveService,
                                      @Autowired private val sharedGroupInviteService: SharedGroupInviteService,
                                      @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                      @Autowired private val testAccountInserter: TestAccountInserter) {
    private val sharedGroupService: SharedGroupService = SharedGroupService(sharedGroupRepository,
                                                                            localDateTimeProvider,
                                                                            sharedGroupInviteService,
                                                                            sharedGroupLeaveService)

    private lateinit var userSession: UserSession
    private lateinit var user1AccountId: AccountId
    private val sharedGroupInviteFormCommand = SharedGroupInviteFormCommand("user@example.co.jp")

    @BeforeEach
    internal fun setUp() {
        val requesterAccountId = testAccountInserter.insertAccountAndProfile().first.id
        userSession = UserSessionFactory.create(requesterAccountId)
        user1AccountId = createAccount().id

        clearMocks(emailSenderClient)
    }

    @AfterEach
    fun tearDown() {
        clearMocks(emailSenderClient)
    }

    @Nested
    inner class InviteToSharedGroupTest {
        @Test
        @DisplayName("共有グループに招待する")
        fun inviteToSharedGroup() {
            //given:
            val members = setOf(userSession.accountId, user1AccountId)
            val sharedGroup = testSharedGroupInserter.insert(members = members)

            //when:
            sharedGroupService.inviteToSharedGroup(sharedGroupInviteFormCommand, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
            assertThat(foundSharedGroup?.members).containsExactlyInAnyOrder(*members.toTypedArray())
            assertThat(foundSharedGroup?.pendingInvitations).hasSize(1)
            verify(exactly = 1) { emailSenderClient.send(any()) }
        }

        @Test
        @DisplayName("参加している共有グループが見つからなかった場合、共有グループを作成して招待する")
        fun joinedSharedGroupNotFound_createAndInviteToNewSharedGroup() {
            //when:
            val createdSharedGroup = sharedGroupService.inviteToSharedGroup(sharedGroupInviteFormCommand, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(createdSharedGroup)
            assertThat(foundSharedGroup?.members).contains(userSession.accountId)
            assertThat(foundSharedGroup?.pendingInvitations).hasSize(1)
            verify(exactly = 1) { emailSenderClient.send(any()) }
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

    @Nested
    inner class GetJoinableInvitedSharedGroupIdTest {
        @Test
        @DisplayName("参加可能な招待された共有グループIDを取得する")
        fun getJoinableInvitedSharedGroupId() {
            //given:
            val pendingInvitation = SharedGroupFactory.createPendingInvitation()
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(user1AccountId),
                                                             pendingInvitations = setOf(pendingInvitation))

            //when:
            val actual = sharedGroupService.getJoinableInvitedSharedGroupId(pendingInvitation.inviteCode, userSession)

            //then:
            assertThat(actual).isEqualTo(sharedGroup.id)
        }

        @Test
        @DisplayName("招待されている共有グループが見つからなかった場合、参加可能な招待された共有グループIDの取得に失敗する")
        fun invitedSharedGroupNotFound_joinableInvitedSharedGroupRetrievalFails() {
            //when:
            val target: () -> Unit = { sharedGroupService.getJoinableInvitedSharedGroupId("", userSession) }

            //then:
            assertThrows<InvalidInvitationException>(target)
        }

        @Test
        @DisplayName("既にその共有グループに参加している場合、参加可能な招待された共有グループIDの取得に失敗する")
        fun alreadyJoinedShredGroup_joinableInvitedSharedGroupRetrievalFails() {
            //given:
            val pendingInvitation = SharedGroupFactory.createPendingInvitation()
            testSharedGroupInserter.insert(members = setOf(userSession.accountId),
                                           pendingInvitations = setOf(pendingInvitation))

            //when:
            val target: () -> Unit = {
                sharedGroupService.getJoinableInvitedSharedGroupId(pendingInvitation.inviteCode, userSession)
            }

            //then:
            assertThrows<InvalidInvitationException>(target)
        }

        @Test
        @DisplayName("招待コードの有効期限が過ぎている場合、参加可能な招待された共有グループIDの取得に失敗する")
        fun inviteCodeHasExpired_joinableInvitedSharedGroupRetrievalFails() {
            //given:
            val pendingInvitation =
                    SharedGroupFactory.createPendingInvitation(invitedOn = localDateTimeProvider.today().minusDays(9))
            testSharedGroupInserter.insert(members = setOf(createAccount().id),
                                           pendingInvitations = setOf(pendingInvitation))

            //when:
            val target: () -> Unit = {
                sharedGroupService.getJoinableInvitedSharedGroupId(pendingInvitation.inviteCode, userSession)
            }

            //then:
            assertThrows<InvalidInvitationException>(target)
        }
    }

    private fun createAccount() = testAccountInserter.insertAccountAndProfile().first
}
