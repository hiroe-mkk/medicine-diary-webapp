package example.application.service.sharedgroup

import SharedGroupNotFoundException
import example.application.service.account.*
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
                                      @Autowired private val accountRepository: AccountRepository,
                                      @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                      @Autowired private val testAccountInserter: TestAccountInserter) {
    private val shareRequestService: SharedGroupParticipationService =
            SharedGroupParticipationService(sharedGroupRepository, profileRepository)
    private val sharedGroupService: SharedGroupService =
            SharedGroupService(sharedGroupRepository, accountRepository, shareRequestService)

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
        @DisplayName("共有する")
        fun share() {
            //when:
            val sharedGroupId = sharedGroupService.share(user1AccountId, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroupId)
            assertThat(foundSharedGroup?.members).containsExactlyInAnyOrder(userSession.accountId)
            assertThat(foundSharedGroup?.invitees).containsExactlyInAnyOrder(user1AccountId)
        }

        @Test
        @DisplayName("既に共有グループに参加している場合、共有に失敗する")
        fun participatingInShredGroup_sharingFails() {
            //given:
            testSharedGroupInserter.insert(members = setOf(userSession.accountId))

            //when:
            val target: () -> Unit = { sharedGroupService.share(user1AccountId, userSession) }

            //then:
            assertThrows<ShareException>(target)
        }

        @Test
        @DisplayName("ユーザー名が設定されていない場合、共有に失敗する")
        fun usernameIsNotSet_sharingFails() {
            //given:
            val profile = profileRepository.findByAccountId(userSession.accountId)!!
            profile.changeUsername(Username(""))
            profileRepository.save(profile)

            //when:
            val target: () -> Unit = { sharedGroupService.share(user1AccountId, userSession) }

            //then:
            assertThrows<ShareException>(target)
        }

        @Test
        @DisplayName("アカウントが見つからなかった場合、共有グループへの招待に失敗する")
        fun accountNotFound_sharingFails() {
            //given:
            val badAccountId = AccountId("NonexistentId")

            //when:
            val target: () -> Unit = {
                sharedGroupService.share(badAccountId, userSession)
            }

            //then:
            val accountNotFoundException = assertThrows<AccountNotFoundException>(target)
            assertThat(accountNotFoundException.accountId).isEqualTo(badAccountId)
        }
    }

    @Nested
    inner class InviteToSharedGroupTest {
        @Test
        @DisplayName("共有グループに招待する")
        fun inviteToSharedGroup() {
            //given:
            val members = setOf(userSession.accountId, createAccount().id)
            val sharedGroup = testSharedGroupInserter.insert(members = members)

            //when:
            sharedGroupService.inviteToSharedGroup(sharedGroup.id, user1AccountId, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
            assertThat(foundSharedGroup?.members).containsExactlyInAnyOrder(*members.toTypedArray())
            assertThat(foundSharedGroup?.invitees).containsExactlyInAnyOrder(user1AccountId)
        }

        @Test
        @DisplayName("共有グループが見つからなかった場合、共有グループへの招待に失敗する")
        fun sharedGroupNotFound_invitingToSharedGroupFails() {
            //given:
            val badSharedGroupId = SharedGroupId("NonexistentId")

            //when:
            val target: () -> Unit = {
                sharedGroupService.inviteToSharedGroup(badSharedGroupId, user1AccountId, userSession)
            }

            //then:
            val sharedGroupNotFoundException = assertThrows<SharedGroupNotFoundException>(target)
            assertThat(sharedGroupNotFoundException.sharedGroupId).isEqualTo(badSharedGroupId)
        }

        @Test
        @DisplayName("アカウントが見つからなかった場合、共有グループへの招待に失敗する")
        fun accountNotFound_invitingToSharedGroupFails() {
            //given:
            val badAccountId = AccountId("NonexistentId")
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(userSession.accountId,
                                                                             createAccount().id))

            //when:
            val target: () -> Unit = {
                sharedGroupService.inviteToSharedGroup(sharedGroup.id, badAccountId, userSession)
            }

            //then:
            val accountNotFoundException = assertThrows<AccountNotFoundException>(target)
            assertThat(accountNotFoundException.accountId).isEqualTo(badAccountId)
        }

        @Test
        @DisplayName("共有グループに参加していない場合、共有グループへの招待に失敗する")
        fun notParticipatingInSharedGroup_invitingToSharedGroupFails() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(createAccount().id,
                                                                             createAccount().id))

            //when:
            val target: () -> Unit = {
                sharedGroupService.inviteToSharedGroup(sharedGroup.id, user1AccountId, userSession)
            }

            //then:
            val sharedGroupNotFoundException = assertThrows<SharedGroupNotFoundException>(target)
            assertThat(sharedGroupNotFoundException.sharedGroupId).isEqualTo(sharedGroup.id)
        }

        @Test
        @DisplayName("対象ユーザーが既に指定された共有グループに参加している場合、共有グループへの招待に失敗する")
        fun targetIsParticipatingInShredGroup_invitingToSharedGroupFails() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(userSession.accountId,
                                                                             user1AccountId))

            //when:
            val target: () -> Unit = {
                sharedGroupService.inviteToSharedGroup(sharedGroup.id, user1AccountId, userSession)
            }

            //then:
            assertThrows<InvitationToSharedGroupException>(target)
        }

        @Test
        @DisplayName("対象ユーザーが既に指定された共有グループに招待されている場合、共有グループへの招待に失敗する")
        fun targetIsInvitedToShredGroup_invitingToSharedGroupFails() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(userSession.accountId),
                                                             invitees = setOf(user1AccountId))

            //when:
            val target: () -> Unit = {
                sharedGroupService.inviteToSharedGroup(sharedGroup.id, user1AccountId, userSession)
            }

            //then:
            assertThrows<InvitationToSharedGroupException>(target)
        }
    }

    @Nested
    inner class RejectInvitationToSharedGroupTest {
        @Test
        @DisplayName("共有グループへの招待を拒否する")
        fun rejectInvitation() {
            //given:
            val members = setOf(user1AccountId, createAccount().id)
            val sharedGroup = testSharedGroupInserter.insert(members = members,
                                                             invitees = setOf(userSession.accountId))

            //when:
            sharedGroupService.rejectInvitationToSharedGroup(sharedGroup.id, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
            assertThat(foundSharedGroup?.members).containsExactlyInAnyOrder(*members.toTypedArray())
            assertThat(foundSharedGroup?.invitees).isEmpty()
        }

        @Test
        @DisplayName("条件を満たす場合、共有グループは削除される")
        fun conditionsAreMet_SharedGroupIsDeleted() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(user1AccountId),
                                                             invitees = setOf(userSession.accountId))

            //when:
            sharedGroupService.rejectInvitationToSharedGroup(sharedGroup.id, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
            assertThat(foundSharedGroup).isNull()
        }

        @Test
        @DisplayName("共有グループが見つからなかった場合、共有グループへの招待の拒否に失敗する")
        fun sharedGroupNotFound_rejectingInvitationToSharedGroupFails() {
            //given:
            val badSharedGroupId = SharedGroupId("NonexistentId")

            //when:
            val target: () -> Unit =
                    { sharedGroupService.rejectInvitationToSharedGroup(badSharedGroupId, userSession) }

            //then:
            val sharedGroupNotFoundException = assertThrows<SharedGroupNotFoundException>(target)
            assertThat(sharedGroupNotFoundException.sharedGroupId).isEqualTo(badSharedGroupId)
        }

        @Test
        @DisplayName("共有グループに招待されていない場合、招待の拒否に失敗する")
        fun notInvitedToSharedGroup_rejectingInvitationToSharedGroupFails() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert()

            //when:
            val target: () -> Unit = { sharedGroupService.rejectInvitationToSharedGroup(sharedGroup.id, userSession) }

            //then:
            val sharedGroupNotFoundException = assertThrows<SharedGroupNotFoundException>(target)
            assertThat(sharedGroupNotFoundException.sharedGroupId).isEqualTo(sharedGroup.id)
        }
    }

    @Nested
    inner class CancelInvitationToSharedGroupTest {
        @Test
        @DisplayName("共有グループへの招待を取り消す")
        fun cancelInvitation() {
            //given:
            val members = setOf(userSession.accountId, createAccount().id)
            val sharedGroup = testSharedGroupInserter.insert(members = members,
                                                             invitees = setOf(user1AccountId))

            //when:
            sharedGroupService.cancelInvitationToSharedGroup(sharedGroup.id, user1AccountId, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
            assertThat(foundSharedGroup?.members).containsExactlyInAnyOrder(*members.toTypedArray())
            assertThat(foundSharedGroup?.invitees).isEmpty()
        }

        @Test
        @DisplayName("条件を満たす場合、共有グループは削除される")
        fun conditionsAreMet_SharedGroupIsDeleted() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(userSession.accountId),
                                                             invitees = setOf(user1AccountId))

            //when:
            sharedGroupService.cancelInvitationToSharedGroup(sharedGroup.id, user1AccountId, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
            assertThat(foundSharedGroup).isNull()
        }

        @Test
        @DisplayName("共有グループが見つからなかった場合、共有グループへの招待の取り消しに失敗する")
        fun sharedGroupNotFound_cancelingInvitationToSharedGroupFails() {
            //given:
            val badSharedGroupId = SharedGroupId("NonexistentId")

            //when:
            val target: () -> Unit = {
                sharedGroupService.cancelInvitationToSharedGroup(badSharedGroupId, user1AccountId, userSession)
            }

            //then:
            val sharedGroupNotFoundException = assertThrows<SharedGroupNotFoundException>(target)
            assertThat(sharedGroupNotFoundException.sharedGroupId).isEqualTo(badSharedGroupId)
        }

        @Test
        @DisplayName("共有グループに参加していない場合、共有グループへの招待の取り消しに失敗する")
        fun notParticipatingInSharedGroup_cancelingInvitationToSharedGroupFails() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert()

            //when:
            val target: () -> Unit = {
                sharedGroupService.cancelInvitationToSharedGroup(sharedGroup.id, user1AccountId, userSession)
            }

            //then:
            val sharedGroupNotFoundException = assertThrows<SharedGroupNotFoundException>(target)
            assertThat(sharedGroupNotFoundException.sharedGroupId).isEqualTo(sharedGroup.id)
        }
    }

    @Nested
    inner class ParticipateInSharedGroupTest {
        @Test
        @DisplayName("共有グループに参加する")
        fun participateInSharedGroup() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(user1AccountId),
                                                             invitees = setOf(userSession.accountId))

            //when:
            sharedGroupService.participateInSharedGroup(sharedGroup.id, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
            assertThat(foundSharedGroup?.members).containsExactlyInAnyOrder(user1AccountId, userSession.accountId)
            assertThat(foundSharedGroup?.invitees).isEmpty()
        }

        @Test
        @DisplayName("共有グループが見つからなかった場合、共有グループへの参加に失敗する")
        fun sharedGroupNotFound_participatingInSharedGroupFails() {
            //given:
            val badSharedGroupId = SharedGroupId("NonexistentId")

            //when:
            val target: () -> Unit = { sharedGroupService.participateInSharedGroup(badSharedGroupId, userSession) }

            //then:
            val sharedGroupNotFoundException = assertThrows<SharedGroupNotFoundException>(target)
            assertThat(sharedGroupNotFoundException.sharedGroupId).isEqualTo(badSharedGroupId)
        }

        @Test
        @DisplayName("共有グループに招待されていない場合、共有グループへの参加に失敗する")
        fun notInvitedToSharedGroup_participatingInSharedGroupFails() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(createAccount().id,
                                                                             createAccount().id))

            //when:
            val target: () -> Unit = { sharedGroupService.participateInSharedGroup(sharedGroup.id, userSession) }

            //then:
            val sharedGroupNotFoundException = assertThrows<SharedGroupNotFoundException>(target)
            assertThat(sharedGroupNotFoundException.sharedGroupId).isEqualTo(sharedGroup.id)
        }

        @Test
        @DisplayName("既に共有グループに参加している場合、共有グループへの参加に失敗する")
        fun participatingInShredGroup_participatingInSharedGroupFails() {
            //given:
            testSharedGroupInserter.insert(members = setOf(userSession.accountId, createAccount().id))
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(createAccount().id,
                                                                             createAccount().id),
                                                             invitees = setOf(userSession.accountId))

            //when:
            val target: () -> Unit = { sharedGroupService.participateInSharedGroup(sharedGroup.id, userSession) }

            //then:
            assertThrows<ParticipationInSharedGroupException>(target)
        }

        @Test
        @DisplayName("ユーザー名が設定されていない場合、共有グループへの参加に失敗する")
        fun usernameIsNotSet_participatingInSharedGroupFails() {
            //given:
            val profile = profileRepository.findByAccountId(userSession.accountId)!!
            profile.changeUsername(Username(""))
            profileRepository.save(profile)
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(createAccount().id,
                                                                             createAccount().id),
                                                             invitees = setOf(userSession.accountId))

            //when:
            val target: () -> Unit = { sharedGroupService.participateInSharedGroup(sharedGroup.id, userSession) }

            //then:
            assertThrows<ParticipationInSharedGroupException>(target)
        }
    }

    @Nested
    inner class UnshareTest {
        @Test
        @DisplayName("共有を解除する")
        fun unshareSharedGroup() {
            //given:
            val members = setOf(userSession.accountId, user1AccountId, createAccount().id)
            val sharedGroup = testSharedGroupInserter.insert(members = members)

            //when:
            sharedGroupService.unshare(sharedGroup.id, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
            val expected = members - userSession.accountId
            assertThat(foundSharedGroup?.members).containsExactlyInAnyOrder(*expected.toTypedArray())
            assertThat(foundSharedGroup?.invitees).isEmpty()
        }

        @Test
        @DisplayName("条件を満たす場合、共有グループは削除される")
        fun conditionsAreMet_SharedGroupIsDeleted() {
            //given:
            val members = setOf(userSession.accountId, user1AccountId)
            val sharedGroup = testSharedGroupInserter.insert(members = members)

            //when:
            sharedGroupService.unshare(sharedGroup.id, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
            assertThat(foundSharedGroup).isNull()
        }

        @Test
        @DisplayName("共有グループが見つからなかった場合、共有解除に失敗する")
        fun sharedGroupNotFound_unsharingFails() {
            //given:
            val badSharedGroupId = SharedGroupId("NonexistentId")

            //when:
            val target: () -> Unit = {
                sharedGroupService.unshare(badSharedGroupId, userSession)
            }

            //then:
            val sharedGroupNotFoundException = assertThrows<SharedGroupNotFoundException>(target)
            assertThat(sharedGroupNotFoundException.sharedGroupId).isEqualTo(badSharedGroupId)
        }


        @Test
        @DisplayName("共有グループに参加していない場合、共有解除に失敗する")
        fun notParticipatingInSharedGroup_unsharingFails() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert()

            //when:
            val target: () -> Unit = {
                sharedGroupService.unshare(sharedGroup.id, userSession)
            }

            //then:
            val sharedGroupNotFoundException = assertThrows<SharedGroupNotFoundException>(target)
            assertThat(sharedGroupNotFoundException.sharedGroupId).isEqualTo(sharedGroup.id)
        }
    }

    private fun createAccount() = testAccountInserter.insertAccountAndProfile().first
}