package example.presentation.controller.api.sharedgroup

import example.domain.model.sharedgroup.*
import example.domain.shared.type.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import example.testhelper.springframework.security.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ControllerTest
internal class SharedGroupRejectApiControllerTest(@Autowired private val mockMvc: MockMvc,
                                                  @Autowired private val testAccountInserter: TestAccountInserter,
                                                  @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                                  @Autowired private val localDateTimeProvider: LocalDateTimeProvider) {

    companion object {
        private const val PATH = "/api/shared-group/reject"
    }

    private lateinit var pendingInvitation: PendingInvitation

    @BeforeEach
    internal fun setUp() {
        pendingInvitation = SharedGroupFactory.createPendingInvitation(invitedOn = localDateTimeProvider.today())
        testSharedGroupInserter.insert(members = setOf(testAccountInserter.insertAccountAndProfile().first.id),
                                       pendingInvitations = setOf(pendingInvitation))
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("共有グループへの招待の拒否に成功した場合、ステータスコード204のレスポンスを返す")
    fun sharedGroupRejectSucceeds_returnsResponseWithStatus204() {
        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("code", pendingInvitation.inviteCode))

        //then:
        actions.andExpect(status().isNoContent)
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("共有グループへの招待の拒否に失敗した場合、ステータスコード204のレスポンスを返す")
    fun sharedGroupRejectFails_returnsResponseWithStatus204() {
        //given:
        val invalidInviteCode = ""

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("code", invalidInviteCode))

        //then:
        actions.andExpect(status().isNoContent)
    }

    @Test
    @DisplayName("未認証ユーザによるリクエストの場合、ステータスコード401のレスポンスを返す")
    fun requestedByUnauthenticatedUser_returnsResponseWithStatus401() {
        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("code", pendingInvitation.inviteCode))

        //then:
        actions.andExpect(status().isUnauthorized)
    }
}
