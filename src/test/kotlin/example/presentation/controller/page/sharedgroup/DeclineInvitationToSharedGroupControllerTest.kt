package example.presentation.controller.page.sharedgroup

import example.domain.model.account.*
import example.domain.model.sharedgroup.*
import example.presentation.shared.usersession.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import example.testhelper.springframework.security.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.security.test.web.servlet.request.*
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ControllerTest
internal class DeclineInvitationToSharedGroupControllerTest(@Autowired private val mockMvc: MockMvc,
                                                            @Autowired private val testAccountInserter: TestAccountInserter,
                                                            @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                                            @Autowired private val userSessionProvider: UserSessionProvider) {
    companion object {
        private const val PATH = "/sharedgroup/decline"
    }

    private lateinit var anotherAccountId: AccountId

    @BeforeEach
    internal fun setUp() {
        anotherAccountId = testAccountInserter.insertAccountAndProfile().first.id
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("共有グループへの招待の拒否に成功した場合、共有グループ管理画面にリダイレクトする")
    fun declineInvitationToSharedGroupSucceeds_redirectToShredGroupManagementPage() {
        //given:
        val userSession = userSessionProvider.getUserSession()
        val sharedGroup = testSharedGroupInserter.insert(members = setOf(anotherAccountId),
                                                         invitees = setOf(userSession.accountId))

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("sharedGroupId", sharedGroup.id.value))

        //then:
        actions.andExpect(status().isFound)
            .andExpect(redirectedUrl("/sharedgroup/management"))
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("共有グループへの招待の拒否に失敗した場合、共有グループ管理画面にリダイレクトする")
    fun declineInvitationToSharedGroupFails_redirectToShredGroupManagementPage() {
        //given:
        val userSession = userSessionProvider.getUserSession()
        val sharedGroup = testSharedGroupInserter.insert(members = setOf(anotherAccountId, userSession.accountId),
                                                         invitees = setOf(userSession.accountId))

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("sharedGroupId", sharedGroup.id.value))

        //then:
        actions.andExpect(status().isFound)
            .andExpect(redirectedUrl("/sharedgroup/management"))
    }

    @Test
    @DisplayName("未認証ユーザによるリクエストの場合、トップページ画面にリダイレクトする")
    fun requestedByUnauthenticatedUser_redirectToToppagePage() {
        //given:
        val sharedGroupId = SharedGroupId("sharedGroupId")

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("sharedGroupId", sharedGroupId.value))

        actions.andExpect(status().isFound)
            .andExpect(redirectedUrl("/"))
    }
}