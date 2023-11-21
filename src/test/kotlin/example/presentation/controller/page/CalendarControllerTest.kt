package example.presentation.controller.page

import example.testhelper.springframework.autoconfigure.*
import example.testhelper.springframework.security.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ControllerTest
internal class CalendarControllerTest(@Autowired private val mockMvc: MockMvc) {
    companion object {
        private const val PATH = "/calendar"
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("カレンダー画面を表示する")
    fun displayCalendarPage() {
        //when:
        val actions = mockMvc.perform(get(PATH))

        //then:
        actions.andExpect(status().isOk)
            .andExpect(view().name("calendar"))
    }

    @Test
    @DisplayName("未認証ユーザによるリクエストの場合、ホーム画面へリダイレクトする")
    fun requestedByUnauthenticatedUser_redirectToHomePage() {
        //when:
        val actions = mockMvc.perform(get(PATH))

        //then:
        actions.andExpect(status().isFound)
            .andExpect(redirectedUrl("/"))
    }
}