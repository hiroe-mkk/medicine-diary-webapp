package example.presentation.controller.page

import example.testhelper.springframework.autoconfigure.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ControllerTest
internal class AgreementPageControllerTest(@Autowired private val mockMvc: MockMvc) {
    companion object {
        private const val PATH = "/agreement"
    }

    @Test
    @DisplayName("利用規約画面を表示する")
    fun displayAgreementPage() {
        //when:
        val actions = mockMvc.perform(get(PATH))

        //then:
        actions.andExpect(status().isOk)
            .andExpect(view().name("agreement"))
    }
}