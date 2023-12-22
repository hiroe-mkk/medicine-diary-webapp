package example.presentation.controller.page

import example.presentation.controller.page.medicine.*
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
internal class ContactPageControllerTest(@Autowired private val mockMvc: MockMvc) {
    companion object {
        private const val PATH = "/contact"
    }

    @Nested
    inner class DisplayContactPageTest {
        @Test
        @DisplayName("お問い合わせ画面を表示する")
        fun displayContactPage() {
            //when:
            val actions = mockMvc.perform(get(PATH))

            //then:
            actions.andExpect(status().isOk)
                .andExpect(view().name("contact/form"))
        }
    }

    @Nested
    inner class ConfirmContactContentTest {
        private val emailAddress = "user@example.co.jp"
        private val name = "田中太郎"
        private val content = "お問い合わせ内容"

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("お問い合わせ内容確認画面を表示する")
        fun displayContactContentConfirmationPage() {
            //when:
            val actions = mockMvc.perform(post("${PATH}?confirm")
                                              .with(csrf())
                                              .param("emailAddress", emailAddress)
                                              .param("name", name)
                                              .param("content", content))

            //then:
            actions.andExpect(status().isOk)
                .andExpect(view().name("contact/confirm"))
        }

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("バリデーションエラーが発生した場合、お問い合わせ画面を再表示する")
        fun validationErrorOccurs_redisplayContactPage() {
            //given:
            val invalidEmailAddress = "user @example.co.jp"

            //when:
            val actions = mockMvc.perform(post("${PATH}?confirm")
                                              .with(csrf())
                                              .param("emailAddress", invalidEmailAddress)
                                              .param("name", name)
                                              .param("content", content))

            //then:
            actions.andExpect(status().isOk)
                .andExpect(view().name("contact/form"))
        }
    }
}