package example.presentation.controller.page

import example.testhelper.springframework.autoconfigure.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.test.autoconfigure.web.servlet.*
import org.springframework.boot.test.context.*
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ControllerTest
internal class ToppageControllerTest(@Autowired private val mockMvc: MockMvc) {
    companion object {
        private const val PATH = "/"
    }

    @Test
    @DisplayName("トップページ画面を表示する")
    fun displayToppagePage() {
        //when:
        val actions = mockMvc.perform(get(PATH))

        //then:
        actions.andExpect(status().isOk)
            .andExpect(view().name("toppage"))
    }
}