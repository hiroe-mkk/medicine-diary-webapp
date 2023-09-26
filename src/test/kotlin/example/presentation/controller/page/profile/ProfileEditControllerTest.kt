package example.presentation.controller.page.profile

import example.presentation.controller.page.*
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
internal class ProfileEditControllerTest(@Autowired private val mockMvc: MockMvc) {
    companion object {
        private const val PATH = "/profile/edit"
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("プロフィールの取得に成功した場合、プロフィール編集画面を表示する")
    fun profileAcquisitionSucceeds_displaysProfileEditPage() {
        //when:
        val actions = mockMvc.perform(get(PATH))

        //then:
        actions.andExpect(status().isOk)
            .andExpect(view().name("profile/edit"))
    }

    @Test
    @DisplayName("未認証ユーザによるリクエストの場合、トップページ画面へリダイレクトする")
    fun requestedByUnauthenticatedUser_redirectsToToppagePage() {
        //when:
        val actions = mockMvc.perform(get(PATH))

        //then:
        actions.andExpect(status().isFound)
            .andExpect(redirectedUrl("/"))
    }
}