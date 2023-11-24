package example.presentation.controller.page.account

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
internal class AccountDeletionControllerTest(@Autowired private val mockMvc: MockMvc) {
    companion object {
        private const val PATH = "/account/delete"
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("アカウントの削除に成功した場合、ホーム画面にリダイレクトする")
    fun accountDeletionSucceeds_redirectToHomePage() {
        //when:
        val actions = mockMvc.perform(post(PATH).with(csrf()))

        //then:
        actions.andExpect(status().isFound)
            .andExpect(redirectedUrl("/"))
    }

    @Test
    @DisplayName("未認証ユーザによるリクエストの場合、ホーム画面にリダイレクトする")
    fun requestedByUnauthenticatedUser_redirectToHomePage() {
        //when:
        val actions = mockMvc.perform(post(PATH).with(csrf()))


        actions.andExpect(status().isFound)
            .andExpect(redirectedUrl("/"))
    }
}