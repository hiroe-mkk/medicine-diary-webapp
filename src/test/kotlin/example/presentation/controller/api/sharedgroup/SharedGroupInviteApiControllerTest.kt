package example.presentation.controller.api.sharedgroup

import example.domain.model.sharedgroup.*
import example.infrastructure.email.shared.*
import example.presentation.shared.usersession.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import example.testhelper.springframework.security.*
import io.mockk.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ControllerTest
internal class SharedGroupInviteApiControllerTest(@Autowired private val mockMvc: MockMvc,
                                                  @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                                  @Autowired private val userSessionProvider: UserSessionProvider,
                                                  @Autowired private val emailSenderClient: EmailSenderClient) {
    companion object {
        private const val PATH = "/api/shared-group/invite"
    }

    private val emailAddress = "user@example.co.jp"

    @AfterEach
    fun tearDown() {
        clearMocks(emailSenderClient)
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("共有グループへの招待に成功した場合、ステータスコード204のレスポンスを返す")
    fun sharedGroupInviteSucceeds_returnsResponseWithStatus204() {
        //given:
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        testSharedGroupInserter.insert(members = setOf(userSession.accountId),
                                       pendingInvitations = setOf(SharedGroupFactory.createPendingInvitation()))

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("emailAddress", emailAddress))

        //then:
        actions.andExpect(status().isNoContent)
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("共有グループへの招待に失敗した場合、ステータスコード409のレスポンスを返す")
    fun sharedGroupInviteFails_returnsResponseWithStatus409() {
        //given:
        every { emailSenderClient.send(any()) } throws SharedGroupInviteFailedException("Failed to send email.")

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("emailAddress", emailAddress))

        //then:
        actions.andExpect(status().isConflict)
    }

    @Test
    @DisplayName("未認証ユーザによるリクエストの場合、ステータスコード401のレスポンスを返す")
    fun requestedByUnauthenticatedUser_returnsResponseWithStatus401() {
        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("emailAddress", emailAddress))

        //then:
        actions.andExpect(status().isUnauthorized)
    }
}
