package example.presentation.controller.page.service

import example.testhelper.springframework.autoconfigure.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ControllerTest
internal class ServiceControllerTest(@Autowired private val mockMvc: MockMvc) {
    companion object {
        private const val PATH = "/service"
    }

    @Test
    @DisplayName("利用規約画面を表示する")
    fun displayAgreementPage() {
        //when:
        val actions = mockMvc.perform(get("${PATH}/agreement"))

        //then:
        actions.andExpect(status().isOk)
            .andExpect(view().name("service/agreement"))
    }
}