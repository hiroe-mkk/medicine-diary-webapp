package example.presentation.controller.page.sharedgroup

import example.domain.model.account.*
import example.domain.model.medicine.*
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
internal class ShareControllerTest(@Autowired private val mockMvc: MockMvc,
                                   @Autowired private val testAccountInserter: TestAccountInserter,
                                   @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                   @Autowired private val userSessionProvider: UserSessionProvider) {
    companion object {
        private const val PATH = "/sharedgroup/share"
    }

    private lateinit var anotherAccountId: AccountId

    @BeforeEach
    internal fun setUp() {
        anotherAccountId = testAccountInserter.insertAccountAndProfile().first.id
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("共有に成功した場合、共有グループ管理画面にリダイレクトする")
    fun shareSucceeds_redirectToShredGroupManagementPage() {
        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("accountId", anotherAccountId.value))

        //then:
        actions.andExpect(status().isFound)
            .andExpect(redirectedUrl("/sharedgroup/management"))
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("共有に失敗した場合、共有グループ管理画面にリダイレクトする")
    fun shareFails_redirectToShredGroupManagementPage() {
        //given:
        val userSession = userSessionProvider.getUserSession()
        testSharedGroupInserter.insert(members = setOf(userSession.accountId))

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("accountId", anotherAccountId.value))

        //then:
        actions.andExpect(status().isFound)
            .andExpect(redirectedUrl("/sharedgroup/management"))
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("アカウントが見つからなかった場合、NotFoundエラー画面を表示する")
    fun accountNotFound_displayNotFoundErrorPage() {
        //given:
        val badAccountId = AccountId("NonexistentId")

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("accountId", badAccountId.value))

        //then:
        actions.andExpect(status().isNotFound)
            .andExpect(view().name("error/notFoundError"))
    }

    @Test
    @DisplayName("未認証ユーザによるリクエストの場合、トップページ画面にリダイレクトする")
    fun requestedByUnauthenticatedUser_redirectToToppagePage() {
        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("accountId", anotherAccountId.value))

        actions.andExpect(status().isFound)
            .andExpect(redirectedUrl("/"))
    }
}