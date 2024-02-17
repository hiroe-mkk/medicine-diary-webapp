package example.presentation.controller.page

import example.domain.model.account.*
import example.presentation.shared.usersession.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import example.testhelper.springframework.security.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ControllerTest
internal class UserPageControllerTest(@Autowired private val mockMvc: MockMvc,
                                      @Autowired private val testAccountInserter: TestAccountInserter,
                                      @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                      @Autowired private val userSessionProvider: UserSessionProvider) {
    companion object {
        private const val PATH = "/users/{accountId}"
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("ユーザー画面を表示する")
    fun displayUserPage() {
        //given:
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        val member = testAccountInserter.insertAccountAndProfile().first.id
        testSharedGroupInserter.insert(members = setOf(userSession.accountId, member))

        //when:
        val actions = mockMvc.perform(get(PATH, member))

        //then:
        actions.andExpect(status().isOk)
            .andExpect(view().name("user"))
    }

    @Test
    @DisplayName("未認証ユーザによるリクエストの場合、ホーム画面へリダイレクトする")
    fun requestedByUnauthenticatedUser_redirectToHomePage() {
        //given:
        val accountId = AccountId("accountId")

        //when:
        val actions = mockMvc.perform(get(PATH, accountId))

        //then:
        actions.andExpect(status().isFound)
            .andExpect(redirectedUrl("/"))
    }
}