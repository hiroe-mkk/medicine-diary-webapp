package example.application.service.sharedgroup

import SharedGroupNotFoundException
import example.application.service.medicine.*
import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.sharedgroup.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.context.annotation.*

@MyBatisRepositoryTest
internal class SharedGroupServiceTest(@Autowired private val sharedGroupRepository: SharedGroupRepository,
                                      @Autowired private val profileRepository: ProfileRepository,
                                      @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                      @Autowired private val testAccountInserter: TestAccountInserter) {
    private val shareRequestService: ShareRequestService =
            ShareRequestService(sharedGroupRepository, profileRepository)
    private val sharedGroupService: SharedGroupService =
            SharedGroupService(sharedGroupRepository, shareRequestService)

    private lateinit var userSession: UserSession
    private lateinit var anotherAccountId: AccountId


    @BeforeEach
    internal fun setUp() {
        val (account, _) = testAccountInserter.insertAccountAndProfile()
        userSession = UserSessionFactory.create(account.id)
        anotherAccountId = testAccountInserter.insertAccountAndProfile().first.id
    }

    @Nested
    inner class RequestToShareTest {
        @Test
        @DisplayName("共有をリクエストする")
        fun requestToShare() {
            //when:
            val sharedGroupId = sharedGroupService.requestToShare(anotherAccountId, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroupId)
            assertThat(foundSharedGroup?.members).containsExactlyInAnyOrder(userSession.accountId)
            assertThat(foundSharedGroup?.pendingUsers).containsExactlyInAnyOrder(anotherAccountId)
        }

        @Test
        @DisplayName("ユーザーが共有グループに参加している場合、共有のリクエストに失敗する")
        fun whenParticipatingInShredGroup_requestingToShareFails() {
            //given:
            testSharedGroupInserter.insert(members = setOf(userSession.accountId))

            //when:
            val target: () -> Unit = { sharedGroupService.requestToShare(anotherAccountId, userSession) }

            //then:
            assertThrows<ShareRequestFailedException>(target)
        }

        @Test
        @DisplayName("ユーザーのユーザー名が設定されていない場合、共有のリクエストに失敗する")
        fun whenUsernameIsNotSet_requestingToShareFails() {
            //given:
            val profile = profileRepository.findByAccountId(userSession.accountId)!!
            profile.changeUsername(Username(""))
            profileRepository.save(profile)

            //when:
            val target: () -> Unit = { sharedGroupService.requestToShare(anotherAccountId, userSession) }

            //then:
            assertThrows<ShareRequestFailedException>(target)
        }
    }

    @Nested
    inner class InviteToSharedGroupTest {
        @Test
        @DisplayName("共有グループに招待する")
        fun inviteToSharedGroup() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(userSession.accountId))

            //when:
            sharedGroupService.inviteToSharedGroup(sharedGroup.id, anotherAccountId, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
            assertThat(foundSharedGroup?.members).containsExactlyInAnyOrder(userSession.accountId)
            assertThat(foundSharedGroup?.pendingUsers).containsExactlyInAnyOrder(anotherAccountId)
        }

        @Test
        @DisplayName("共有グループが見つからなかった場合、共有グループへの招待に失敗する")
        fun whenSharedGroupNotFound_invitingToSharedGroupFails() {
            //given:
            val badSharedGroupId = SharedGroupId("NonexistentId")

            //when:
            val target: () -> Unit = {
                sharedGroupService.inviteToSharedGroup(badSharedGroupId, anotherAccountId, userSession)
            }

            //then:
            val sharedGroupNotFoundException = assertThrows<SharedGroupNotFoundException>(target)
            assertThat(sharedGroupNotFoundException.sharedGroupId).isEqualTo(badSharedGroupId)
        }

        @Test
        @DisplayName("ユーザーが共有グループに参加していない場合、共有グループへの招待に失敗する")
        fun whenNotParticipatingInSharedGroup_invitingToSharedGroupFails() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert()

            //when:
            val target: () -> Unit = {
                sharedGroupService.inviteToSharedGroup(sharedGroup.id, anotherAccountId, userSession)
            }

            //then:
            assertThrows<SharedGroupNotFoundException>(target)
        }

        @Test
        @DisplayName("対象ユーザーが指定された共有グループに参加している場合、共有グループへの招待に失敗する")
        fun whenTargetIsParticipatingInShredGroup_invitingToSharedGroupFails() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(userSession.accountId, anotherAccountId))

            //when:
            val target: () -> Unit = {
                sharedGroupService.inviteToSharedGroup(sharedGroup.id, anotherAccountId, userSession)
            }

            //then:
            assertThrows<InvitationToSharedGroupFailedException>(target)
        }

        @Test
        @DisplayName("対象ユーザーが指定された共有グループに招待されている場合、共有グループへの招待に失敗する")
        fun whenTargetIsInvitedToShredGroup_invitingToSharedGroupFails() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(userSession.accountId),
                                                             pendingUsers = setOf(anotherAccountId))

            //when:
            val target: () -> Unit = {
                sharedGroupService.inviteToSharedGroup(sharedGroup.id, anotherAccountId, userSession)
            }

            //then:
            assertThrows<InvitationToSharedGroupFailedException>(target)
        }
    }
}