package example.presentation.controller.page

import example.testhelper.springframework.autoconfigure.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ControllerTest
internal class ContactPageControllerTest(@Autowired private val mockMvc: MockMvc) {
    companion object {
        private const val PATH = "/contact"
    }

    private val emailAddress = "user@example.co.jp"
    private val invalidEmailAddress = "user @example.co.jp"
    private val name = "田中太郎"
    private val content = "お問い合わせ内容"

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
    inner class RedisplayContactPageTest {
        @Test
        @DisplayName("お問い合わせ画面を再表示する")
        fun redisplayContactPage() {
            //when:
            val actions = mockMvc.perform(post("${PATH}?redo")
                                              .with(csrf())
                                              .param("emailAddress", emailAddress)
                                              .param("name", name)
                                              .param("content", content))

            //then:
            actions.andExpect(status().isOk)
                .andExpect(view().name("contact/form"))
        }
    }

    @Nested
    inner class ConfirmContactContentTest {
        @Test
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
        @DisplayName("バリデーションエラーが発生した場合、お問い合わせ画面を再表示する")
        fun validationErrorOccurs_redisplayContactPage() {
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

    @Nested
    inner class SendContactFormTest {
        @Test
        @DisplayName("お問い合わせフォーム送信に成功した場合、お問い合わせ完了画面を表示する")
        fun contactFormSendSucceeds_displayContactCompletionPage() {
            //when:
            val actions = mockMvc.perform(post(PATH)
                                              .with(csrf())
                                              .param("emailAddress", emailAddress)
                                              .param("name", name)
                                              .param("content", content))

            //then:
            actions.andExpect(status().isFound)
                .andExpect(redirectedUrl("/contact?complete"))
        }

        @Test
        @DisplayName("バリデーションエラーが発生した場合、お問い合わせ画面を再表示する")
        fun validationErrorOccurs_redisplayContactPage() {
            //when:
            val actions = mockMvc.perform(post(PATH)
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