package example.presentation.controller.page.takingrecord

import example.presentation.controller.page.medicine.*
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
internal class TakingRecordAdditionControllerTest(@Autowired private val mockMvc: MockMvc) {
    companion object {
        private const val PATH = "/takingrecords/add"
    }

    @Nested
    inner class DisplayTakingRecordAdditionPageTest {
        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("服用記録追加画面を表示する")
        fun displayTakingRecordAdditionPage() {
            //when:
            val actions = mockMvc.perform(get(PATH))

            //then:
            actions.andExpect(status().isOk)
                .andExpect(view().name("takingrecord/form"))
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
}