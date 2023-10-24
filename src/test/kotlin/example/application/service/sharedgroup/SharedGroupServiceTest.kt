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
    private val shareRequestService: example.domain.model.sharedgroup.SharedGroupDomainService =
            SharedGroupDomainService(sharedGroupRepository, profileRepository)
    private val sharedGroupService: SharedGroupService =
            SharedGroupService(sharedGroupRepository, shareRequestService)

    private lateinit var userSession: UserSession
    private lateinit var anotherAccountId: AccountId


    @BeforeEach
    internal fun setUp() {
        val (account, _) = testAccountInserter.insertAccountAndProfile()
        userSession = UserSessionFactory.create(account.id)
        anotherAccountId = createAnotherAccount().id
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
            val sharedGroupNotFoundException = assertThrows<SharedGroupNotFoundException>(target)
            assertThat(sharedGroupNotFoundException.sharedGroupId).isEqualTo(sharedGroup.id)
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

    @Nested
    inner class DeclineInvitation {
        @Test
        @DisplayName("招待を拒否する")
        fun declineInvitation() {
            //given:
            val members = setOf(anotherAccountId, createAnotherAccount().id)
            val sharedGroup = testSharedGroupInserter.insert(members = members,
                                                             pendingUsers = setOf(userSession.accountId))

            //when:
            sharedGroupService.declineInvitation(sharedGroup.id, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
            assertThat(foundSharedGroup?.members).containsExactlyInAnyOrder(*members.toTypedArray())
            assertThat(foundSharedGroup?.pendingUsers).isEmpty()
        }

        @Test
        @DisplayName("メンバーと参加待ちユーザーの合計が1人以下の場合、共有グループは削除される")
        fun whenMembersSizePlusPendingUsersSizeIsLessThanOrEqualTo1_SharedGroupIsDeleted() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(anotherAccountId),
                                                             pendingUsers = setOf(userSession.accountId))

            //when:
            sharedGroupService.declineInvitation(sharedGroup.id, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
            assertThat(foundSharedGroup).isNull()
        }

        @Test
        @DisplayName("共有グループが見つからなかった場合、共有グループへの招待の拒否に失敗する")
        fun whenSharedGroupNotFound_decliningInvitationToSharedGroupFails() {
            //given:
            val badSharedGroupId = SharedGroupId("NonexistentId")

            //when:
            val target: () -> Unit = { sharedGroupService.declineInvitation(badSharedGroupId, userSession) }

            //then:
            val sharedGroupNotFoundException = assertThrows<SharedGroupNotFoundException>(target)
            assertThat(sharedGroupNotFoundException.sharedGroupId).isEqualTo(badSharedGroupId)
        }

        @Test
        @DisplayName("ユーザーが共有グループに招待されていない場合、招待の拒否に失敗する")
        fun whenNotInvitedToSharedGroup_decliningInvitationToSharedGroupFails() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert()

            //when:
            val target: () -> Unit = { sharedGroupService.declineInvitation(sharedGroup.id, userSession) }

            //then:
            val sharedGroupNotFoundException = assertThrows<SharedGroupNotFoundException>(target)
            assertThat(sharedGroupNotFoundException.sharedGroupId).isEqualTo(sharedGroup.id)
        }
    }

    @Nested
    inner class CancelInvitation {
        @Test
        @DisplayName("招待をキャンセルする")
        fun cancelInvitation() {
            //given:
            val members = setOf(userSession.accountId, createAnotherAccount().id)
            val sharedGroup = testSharedGroupInserter.insert(members = members,
                                                             pendingUsers = setOf(anotherAccountId))

            //when:
            sharedGroupService.cancelInvitation(sharedGroup.id, anotherAccountId, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
            assertThat(foundSharedGroup?.members).containsExactlyInAnyOrder(*members.toTypedArray())
            assertThat(foundSharedGroup?.pendingUsers).isEmpty()
        }

        @Test
        @DisplayName("メンバーと参加待ちユーザーの合計が1人以下の場合、共有グループは削除される")
        fun whenMembersSizePlusPendingUsersSizeIsLessThanOrEqualTo1_SharedGroupIsDeleted() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(userSession.accountId),
                                                             pendingUsers = setOf(anotherAccountId))

            //when:
            sharedGroupService.cancelInvitation(sharedGroup.id, anotherAccountId, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
            assertThat(foundSharedGroup).isNull()
        }

        @Test
        @DisplayName("共有グループが見つからなかった場合、共有グループへの招待のキャンセルに失敗する")
        fun whenSharedGroupNotFound_cancelingInvitationToSharedGroupFails() {
            //given:
            val badSharedGroupId = SharedGroupId("NonexistentId")

            //when:
            val target: () -> Unit = {
                sharedGroupService.cancelInvitation(badSharedGroupId, anotherAccountId, userSession)
            }

            //then:
            val sharedGroupNotFoundException = assertThrows<SharedGroupNotFoundException>(target)
            assertThat(sharedGroupNotFoundException.sharedGroupId).isEqualTo(badSharedGroupId)
        }

        @Test
        @DisplayName("ユーザーが共有グループに参加していない場合、共有グループへの招待のキャンセルに失敗する")
        fun whenNotParticipatingInSharedGroup_cancelingInvitationToSharedGroupFails() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert()

            //when:
            val target: () -> Unit = {
                sharedGroupService.cancelInvitation(sharedGroup.id, anotherAccountId, userSession)
            }

            //then:
            val sharedGroupNotFoundException = assertThrows<SharedGroupNotFoundException>(target)
            assertThat(sharedGroupNotFoundException.sharedGroupId).isEqualTo(sharedGroup.id)
        }
    }

    @Nested
    inner class ParticipateInSharedGroup {
        @Test
        @DisplayName("共有グループに参加する")
        fun participateInSharedGroup() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(anotherAccountId),
                                                             pendingUsers = setOf(userSession.accountId))

            //when:
            sharedGroupService.participateInSharedGroup(sharedGroup.id, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
            assertThat(foundSharedGroup?.members).containsExactlyInAnyOrder(anotherAccountId, userSession.accountId)
            assertThat(foundSharedGroup?.pendingUsers).isEmpty()
        }

        @Test
        @DisplayName("共有グループが見つからなかった場合、共有グループへの参加に失敗する")
        fun whenSharedGroupNotFound_participatingInSharedGroupFails() {
            //given:
            val badSharedGroupId = SharedGroupId("NonexistentId")

            //when:
            val target: () -> Unit = { sharedGroupService.participateInSharedGroup(badSharedGroupId, userSession) }

            //then:
            val sharedGroupNotFoundException = assertThrows<SharedGroupNotFoundException>(target)
            assertThat(sharedGroupNotFoundException.sharedGroupId).isEqualTo(badSharedGroupId)
        }

        @Test
        @DisplayName("ユーザーが共有グループに招待されていない場合、共有グループへの参加に失敗する")
        fun whenNotInvitedToSharedGroup_participatingInSharedGroupFails() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert()

            //when:
            val target: () -> Unit = { sharedGroupService.participateInSharedGroup(sharedGroup.id, userSession) }

            //then:
            val sharedGroupNotFoundException = assertThrows<SharedGroupNotFoundException>(target)
            assertThat(sharedGroupNotFoundException.sharedGroupId).isEqualTo(sharedGroup.id)
        }

        @Test
        @DisplayName("ユーザーが共有グループに参加している場合、共有のリクエストに失敗する")
        fun whenParticipatingInShredGroup_participatingInSharedGroupFails() {
            //given:
            testSharedGroupInserter.insert(members = setOf(userSession.accountId))
            val sharedGroup = testSharedGroupInserter.insert(pendingUsers = setOf(userSession.accountId))

            //when:
            val target: () -> Unit = { sharedGroupService.participateInSharedGroup(sharedGroup.id, userSession) }

            //then:
            assertThrows<ParticipationInSharedGroupFailedException>(target)
        }

        @Test
        @DisplayName("ユーザーのユーザー名が設定されていない場合、共有のリクエストに失敗する")
        fun whenUsernameIsNotSet_participatingInSharedGroupFails() {
            //given:
            val profile = profileRepository.findByAccountId(userSession.accountId)!!
            profile.changeUsername(Username(""))
            profileRepository.save(profile)
            val sharedGroup = testSharedGroupInserter.insert(pendingUsers = setOf(userSession.accountId))

            //when:
            val target: () -> Unit = { sharedGroupService.participateInSharedGroup(sharedGroup.id, userSession) }

            //then:
            assertThrows<ParticipationInSharedGroupFailedException>(target)
        }
    }

    @Nested
    inner class LeaveSharedGroup {
        @Test
        @DisplayName("共有グループから抜ける")
        fun leaveSharedGroup() {
            //given:
            val members = setOf(userSession.accountId, anotherAccountId, createAnotherAccount().id)
            val sharedGroup = testSharedGroupInserter.insert(members = members)

            //when:
            sharedGroupService.leaveSharedGroup(sharedGroup.id, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
            val expected = members - userSession.accountId
            assertThat(foundSharedGroup?.members).containsExactlyInAnyOrder(*expected.toTypedArray())
            assertThat(foundSharedGroup?.pendingUsers).isEmpty()
        }

        @Test
        @DisplayName("メンバーと参加待ちユーザーの合計が1人以下の場合、共有グループは削除される")
        fun whenMembersSizePlusPendingUsersSizeIsLessThanOrEqualTo1_SharedGroupIsDeleted() {
            //given:
            val members = setOf(userSession.accountId, anotherAccountId)
            val sharedGroup = testSharedGroupInserter.insert(members = members)

            //when:
            sharedGroupService.leaveSharedGroup(sharedGroup.id, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
            assertThat(foundSharedGroup).isNull()
        }

        @Test
        @DisplayName("共有グループが見つからなかった場合、共有グループを抜けるのに失敗する")
        fun whenSharedGroupNotFound_leavingSharedGroupFails() {
            //given:
            val badSharedGroupId = SharedGroupId("NonexistentId")

            //when:
            val target: () -> Unit = {
                sharedGroupService.leaveSharedGroup(badSharedGroupId, userSession)
            }

            //then:
            val sharedGroupNotFoundException = assertThrows<SharedGroupNotFoundException>(target)
            assertThat(sharedGroupNotFoundException.sharedGroupId).isEqualTo(badSharedGroupId)
        }


        @Test
        @DisplayName("ユーザーが共有グループに参加していない場合、共有グループを抜けるのに失敗する")
        fun whenNotParticipatingInSharedGroup_leavingSharedGroupFails() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert()

            //when:
            val target: () -> Unit = {
                sharedGroupService.leaveSharedGroup(sharedGroup.id, userSession)
            }

            //then:
            val sharedGroupNotFoundException = assertThrows<SharedGroupNotFoundException>(target)
            assertThat(sharedGroupNotFoundException.sharedGroupId).isEqualTo(sharedGroup.id)
        }
    }

    private fun createAnotherAccount() = testAccountInserter.insertAccountAndProfile().first
}