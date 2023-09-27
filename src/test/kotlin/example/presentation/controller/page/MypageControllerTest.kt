package example.presentation.controller.page

import example.testhelper.springframework.autoconfigure.*
import example.testhelper.springframework.security.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.test.autoconfigure.web.servlet.*
import org.springframework.boot.test.context.*
import org.springframework.security.test.web.servlet.request.*
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ControllerTest
internal class MypageControllerTest(@Autowired private val mockMvc: MockMvc) {
    companion object {
        private const val PATH = "/mypage"
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("マイページ画面を表示する")
    fun displayMypagePage() {
        //when:
        val actions = mockMvc.perform(get(PATH))

        //then:
        actions.andExpect(status().isOk)
            .andExpect(view().name("mypage"))
    }

    @Test
    @DisplayName("未認証ユーザによるリクエストの場合、トップページ画面へリダイレクトする")
    fun requestedByUnauthenticatedUser_redirectToToppagePage() {
        //when:
        val actions = mockMvc.perform(get(PATH))

        //then:
        actions.andExpect(status().isFound)
            .andExpect(redirectedUrl("/"))
    }
}