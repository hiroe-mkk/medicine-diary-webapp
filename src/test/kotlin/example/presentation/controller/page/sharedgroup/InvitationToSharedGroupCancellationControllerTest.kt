package example.presentation.controller.page.sharedgroup

import example.domain.model.account.*
import example.domain.model.medicine.*
import example.domain.model.sharedgroup.*
import example.presentation.controller.page.medicine.*
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
internal class InvitationToSharedGroupCancellationControllerTest(@Autowired private val mockMvc: MockMvc,
                                                                 @Autowired private val testAccountInserter: TestAccountInserter,
                                                                 @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                                                 @Autowired private val userSessionProvider: UserSessionProvider) {
    companion object {
        private const val PATH = "/shared-group/cancel"
    }

    private lateinit var user1AccountId: AccountId

    @BeforeEach
    internal fun setUp() {
        user1AccountId = testAccountInserter.insertAccountAndProfile().first.id
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("共有グループへの招待の取り消しに成功した場合、共有グループ管理画面にリダイレクトする")
    fun cancelInvitationToSharedGroupSucceeds_redirectToShredGroupManagementPage() {
        //given:
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        val sharedGroup = testSharedGroupInserter.insert(members = setOf(userSession.accountId),
                                                         invitees = setOf(user1AccountId))

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("sharedGroupId", sharedGroup.id.value)
                                          .param("accountId", user1AccountId.value))

        //then:
        actions.andExpect(status().isFound)
            .andExpect(redirectedUrl("/shared-group/management"))
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("共有グループが見つからなかった場合、共有グループ管理画面にリダイレクトする")
    fun sharedGroupNotFound_redirectToShredGroupManagementPage() {
        //given:
        val badSharedGroupId = SharedGroupId("NonexistentId")

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("sharedGroupId", badSharedGroupId.value)
                                          .param("accountId", user1AccountId.value))
        //then:
        actions.andExpect(status().isFound)
            .andExpect(redirectedUrl("/shared-group/management"))
    }

    @Test
    @DisplayName("未認証ユーザによるリクエストの場合、ホーム画面にリダイレクトする")
    fun requestedByUnauthenticatedUser_redirectToHomePage() {
        //given:
        val sharedGroupId = SharedGroupId("sharedGroupId")

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("sharedGroupId", sharedGroupId.value)
                                          .param("accountId", user1AccountId.value))

        //then:
        actions.andExpect(status().isFound)
            .andExpect(redirectedUrl("/"))
    }
}