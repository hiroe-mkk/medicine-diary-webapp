package example.application.service.sharedgroup

import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.sharedgroup.*
import example.infrastructure.db.repository.shared.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@DomainLayerTest
internal class SharedGroupServiceTest(@Autowired private val sharedGroupRepository: SharedGroupRepository,
                                      @Autowired private val profileRepository: ProfileRepository,
                                      @Autowired private val accountRepository: AccountRepository,
                                      @Autowired private val sharedGroupQueryService: SharedGroupQueryService,
                                      @Autowired private val sharedGroupJoinService: SharedGroupJoinService,
                                      @Autowired private val sharedGroupLeaveService: SharedGroupLeaveService,
                                      @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                      @Autowired private val testAccountInserter: TestAccountInserter) {
    private val sharedGroupService: SharedGroupService = SharedGroupService(sharedGroupRepository,
                                                                            accountRepository,
                                                                            sharedGroupQueryService,
                                                                            sharedGroupJoinService,
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
            val sharedGroupId = sharedGroupService.createSharedGroup(user1AccountId, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroupId)
            assertThat(foundSharedGroup?.members).containsExactlyInAnyOrder(userSession.accountId)
            assertThat(foundSharedGroup?.invitees).containsExactlyInAnyOrder(user1AccountId)
        }

        @Test
        @DisplayName("既に共有グループに参加している場合、共有グループの作成に失敗する")
        fun JoinedShredGroup_sharedGroupCreationFails() {
            //given:
            testSharedGroupInserter.insert(members = setOf(userSession.accountId))

            //when:
            val target: () -> Unit = { sharedGroupService.createSharedGroup(user1AccountId, userSession) }

            //then:
            assertThrows<SharedGroupCreationFailedException>(target)
        }

        @Test
        @DisplayName("ユーザー名が設定されていない場合、共有グループの作成に失敗する")
        fun usernameIsNotSet_sharedGroupCreationFails() {
            //given:
            val profile = profileRepository.findByAccountId(userSession.accountId)!!
            profile.changeUsername(Username(""))
            profileRepository.save(profile)

            //when:
            val target: () -> Unit = { sharedGroupService.createSharedGroup(user1AccountId, userSession) }

            //then:
            assertThrows<SharedGroupCreationFailedException>(target)
        }

        @Test
        @DisplayName("対象ユーザーが既に指定された共有グループに参加している場合、共有グループの作成に失敗する")
        fun targetIsJoinedShredGroup_sharedGroupCreationFails() {
            //given:
            testSharedGroupInserter.insert(members = setOf(userSession.accountId, user1AccountId))

            //when:
            val target: () -> Unit = {
                sharedGroupService.createSharedGroup(user1AccountId, userSession)
            }

            //then:
            assertThrows<SharedGroupCreationFailedException>(target)
        }

        @Test
        @DisplayName("アカウントが見つからなかった場合、共有グループの作成に失敗する")
        fun accountNotFound_shardGroupCreationFails() {
            //given:
            val nonexistentAccountId = AccountId(EntityIdHelper.generate())

            //when:
            val target: () -> Unit = {
                sharedGroupService.createSharedGroup(nonexistentAccountId, userSession)
            }

            //then:
            assertThrows<SharedGroupCreationFailedException>(target)
        }
    }

    @Nested
    inner class InviteToSharedGroupTest {
        // TODO
/*        @Test
        @DisplayName("共有グループに招待する")
        fun inviteToSharedGroup() {
            //given:
            val members = setOf(userSession.accountId, createAccount().id)
            val sharedGroup = testSharedGroupInserter.insert(members = members)

            //when:
            sharedGroupService.inviteToSharedGroup(sharedGroup.id, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
            assertThat(foundSharedGroup?.members).containsExactlyInAnyOrder(*members.toTypedArray())
            assertThat(foundSharedGroup?.invitees).containsExactlyInAnyOrder(user1AccountId)
        }*/

        @Test
        @DisplayName("参加している共有グループが見つからなかった場合、共有グループへの招待に失敗する")
        fun joinedSharedGroupNotFound_invitingToSharedGroupFails() {
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
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(user1AccountId),
                                                             invitees = setOf(userSession.accountId))

            //when:
            sharedGroupService.joinSharedGroup(sharedGroup.id, userSession)

            //then:
            val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
            assertThat(foundSharedGroup?.members).containsExactlyInAnyOrder(user1AccountId, userSession.accountId)
            assertThat(foundSharedGroup?.invitees).isEmpty()
        }

        @Test
        @DisplayName("招待されている共有グループが見つからなかった場合、共有グループへの参加に失敗する")
        fun invitedSharedGroupNotFound_JoinedSharedGroupFails() {
            //given:
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(createAccount().id,
                                                                             createAccount().id))

            //when:
            val target: () -> Unit = { sharedGroupService.joinSharedGroup(sharedGroup.id, userSession) }

            //then:
            assertThrows<SharedGroupJoinFailedException>(target)
        }

        @Test
        @DisplayName("既に共有グループに参加している場合、共有グループへの参加に失敗する")
        fun joinedShredGroup_JoinedSharedGroupFails() {
            //given:
            testSharedGroupInserter.insert(members = setOf(userSession.accountId, createAccount().id))
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(createAccount().id,
                                                                             createAccount().id),
                                                             invitees = setOf(userSession.accountId))

            //when:
            val target: () -> Unit = { sharedGroupService.joinSharedGroup(sharedGroup.id, userSession) }

            //then:
            assertThrows<SharedGroupJoinFailedException>(target)
        }

        @Test
        @DisplayName("ユーザー名が設定されていない場合、共有グループへの参加に失敗する")
        fun usernameIsNotSet_JoinedSharedGroupFails() {
            //given:
            val profile = profileRepository.findByAccountId(userSession.accountId)!!
            profile.changeUsername(Username(""))
            profileRepository.save(profile)
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(createAccount().id,
                                                                             createAccount().id),
                                                             invitees = setOf(userSession.accountId))

            //when:
            val target: () -> Unit = { sharedGroupService.joinSharedGroup(sharedGroup.id, userSession) }

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
    }

    private fun createAccount() = testAccountInserter.insertAccountAndProfile().first
}
