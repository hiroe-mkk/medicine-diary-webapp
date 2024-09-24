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
class SharedGroupInviteResponseServiceTest(@Autowired private val sharedGroupRepository: SharedGroupRepository,
                                           @Autowired private val localDateTimeProvider: LocalDateTimeProvider,
                                           @Autowired private val sharedGroupFinder: SharedGroupFinder,
                                           @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                           @Autowired private val testAccountInserter: TestAccountInserter) {
    private val sharedGroupService = SharedGroupInviteResponseService(sharedGroupRepository,
                                                                      localDateTimeProvider,
                                                                      sharedGroupFinder)

    private lateinit var userSession: UserSession
    private lateinit var otherUserAccountId: AccountId

    @BeforeEach
    internal fun setUp() {
        userSession = UserSessionFactory.create(testAccountInserter.insertAccountAndProfile().first.id)
        otherUserAccountId = testAccountInserter.insertAccountAndProfile().first.id
    }

    @Nested
    inner class JoinSharedGroupTest {
        @Test
        @DisplayName("共有グループに参加する")
        fun joinSharedGroup() {
            //given:
            val pendingInvitation = SharedGroupFactory.createPendingInvitation()
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(otherUserAccountId),
                                                             pendingInvitations = setOf(pendingInvitation))

            //when:
            sharedGroupService.joinSharedGroup(pendingInvitation.inviteCode, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
            assertThat(foundSharedGroup?.members).containsExactlyInAnyOrder(otherUserAccountId, userSession.accountId)
            assertThat(foundSharedGroup?.pendingInvitations).isEmpty()
        }

        @Test
        @DisplayName("既に共有グループに参加している場合、共有グループへの参加に失敗する")
        fun alreadyJoinedShredGroup_JoinedSharedGroupFails() {
            //given:
            testSharedGroupInserter.insert(members = setOf(userSession.accountId))
            val pendingInvitation = SharedGroupFactory.createPendingInvitation()
            testSharedGroupInserter.insert(members = setOf(otherUserAccountId),
                                           pendingInvitations = setOf(pendingInvitation))

            //when:
            val target: () -> Unit = { sharedGroupService.joinSharedGroup(pendingInvitation.inviteCode, userSession) }

            //then:
            assertThrows<SharedGroupJoinFailedException>(target)
        }

        @Test
        @DisplayName("招待されている共有グループが見つからなかった場合、共有グループへの参加に失敗する")
        fun invitedSharedGroupNotFound_JoinedSharedGroupFails() {
            //given:
            val invalidInviteCode = "code"

            //when:
            val target: () -> Unit = { sharedGroupService.joinSharedGroup(invalidInviteCode, userSession) }

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
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(otherUserAccountId),
                                                             pendingInvitations = setOf(pendingInvitation))

            //when:
            sharedGroupService.rejectInvitationToSharedGroup(pendingInvitation.inviteCode, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
            assertThat(foundSharedGroup?.pendingInvitations).isEmpty()
        }
    }
}
