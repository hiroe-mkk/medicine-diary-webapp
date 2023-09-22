package example.presentation.controller.page

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

@SpringBootTest
@AutoConfigureMockMvc
internal class MypageControllerTest(@Autowired private val mockMvc: MockMvc) {
    companion object {
        private const val PATH = "/mypage"
    }

    @Test
    @DisplayName("マイページ画面を表示する")
    fun displayMypagePage() {
        //when:
        val actions = mockMvc.perform(get(PATH).with(oidcLogin()))

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