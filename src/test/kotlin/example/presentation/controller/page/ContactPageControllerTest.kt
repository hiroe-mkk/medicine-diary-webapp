package example.presentation.controller.page

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
internal class ContactPageControllerTest(@Autowired private val mockMvc: MockMvc) {
    companion object {
        private const val PATH = "/contact"
    }

    @Nested
    inner class DisplayContactPageTest {
        @Test
        @DisplayName("お問い合わせフォーム画面を表示する")
        fun displayContactFormPage() {
            //when:
            val actions = mockMvc.perform(get(PATH))

            //then:
            actions.andExpect(status().isOk)
                .andExpect(view().name("contact/form"))
        }
    }
}