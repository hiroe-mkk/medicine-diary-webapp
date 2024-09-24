package example.application.service.sharedgroup

import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.account.profile.*
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
internal class SharedGroupInviteServiceTest(@Autowired private val sharedGroupRepository: SharedGroupRepository,
                                            @Autowired private val profileRepository: ProfileRepository,
                                            @Autowired private val sharedGroupInviteEmailSender: SharedGroupInviteEmailSender,
                                            @Autowired private val emailSenderClient: EmailSenderClient,
                                            @Autowired private val localDateTimeProvider: LocalDateTimeProvider,
                                            @Autowired private val sharedGroupFinder: SharedGroupFinder,
                                            @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                            @Autowired private val testAccountInserter: TestAccountInserter) {
    private val sharedGroupInviteService = SharedGroupInviteService(sharedGroupRepository,
                                                                    profileRepository,
                                                                    sharedGroupInviteEmailSender,
                                                                    localDateTimeProvider,
                                                                    sharedGroupFinder)

    private lateinit var userSession: UserSession
    private val sharedGroupInviteFormCommand = SharedGroupInviteFormCommand("user@example.co.jp")

    @BeforeEach
    internal fun setUp() {
        userSession = UserSessionFactory.create(testAccountInserter.insertAccountAndProfile().first.id)
        clearMocks(emailSenderClient)
    }

    @Test
    @DisplayName("共有グループに招待する")
    fun inviteToSharedGroup() {
        //given:
        val sharedGroup = testSharedGroupInserter.insert(members = setOf(userSession.accountId))

        //when:
        sharedGroupInviteService.inviteToSharedGroup(sharedGroupInviteFormCommand, userSession)

        //then:
        val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
        assertThat(foundSharedGroup?.members).containsExactlyInAnyOrder(*setOf(userSession.accountId).toTypedArray())
        assertThat(foundSharedGroup?.pendingInvitations).hasSize(1)
        verify(exactly = 1) { emailSenderClient.send(any()) }
    }

    @Test
    @DisplayName("参加している共有グループが見つからなかった場合、共有グループを作成して招待する")
    fun joinedSharedGroupNotFound_createAndInviteToNewSharedGroup() {
        //when:
        val createdSharedGroup = sharedGroupInviteService.inviteToSharedGroup(sharedGroupInviteFormCommand, userSession)

        //then:
        val foundSharedGroup = sharedGroupRepository.findById(createdSharedGroup)
        assertThat(foundSharedGroup?.members).contains(userSession.accountId)
        assertThat(foundSharedGroup?.pendingInvitations).hasSize(1)
        verify(exactly = 1) { emailSenderClient.send(any()) }
    }
}
