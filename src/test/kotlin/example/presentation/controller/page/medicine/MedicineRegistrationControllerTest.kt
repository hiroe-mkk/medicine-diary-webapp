package example.presentation.controller.page.medicine

import example.domain.model.medicine.*
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
internal class MedicineRegistrationControllerTest(@Autowired private val mockMvc: MockMvc) {
    companion object {
        private const val PATH = "/medicines/register"
    }

    @Nested
    inner class DisplayMedicineRegistrationPageTest {
        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("薬登録画面を表示する")
        fun displayMedicineRegistrationPage() {
            //when:
            val actions = mockMvc.perform(get(PATH))

            //then:
            actions.andExpect(status().isOk)
                .andExpect(view().name("medicine/form"))
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

    @Nested
    inner class RegisterMedicineTest {
        private val name = "ロキソニンS"
        private val takingUnit = "錠"
        private val quantity = 1.0
        private val timesPerDay = 3
        private val timingOptions = arrayOf(Timing.AS_NEEDED)
        private val effects = arrayOf("頭痛", "解熱", "肩こり")
        private val precautions = "服用間隔は4時間以上開けること。"

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("薬の登録に成功した場合、薬概要一覧画面にリダイレクトする")
        fun medicineRegistrationSucceeds_redirectToMedicineOverviewsPage() {
            //when:
            val actions = mockMvc.perform(post(PATH)
                                              .with(csrf())
                                              .param("name", name)
                                              .param("quantity", quantity.toString())
                                              .param("takingUnit", takingUnit)
                                              .param("timesPerDay", timesPerDay.toString())
                                              .param("timingOptions", timingOptions[0].name)
                                              .param("effects", effects[0])
                                              .param("effects", effects[1])
                                              .param("effects", effects[2])
                                              .param("precautions", precautions))

            //then:
            actions.andExpect(status().isFound)
                .andExpect(redirectedUrl("/medicines"))
        }

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("バリデーションエラーが発生した場合、薬登録画面を再表示する")
        fun validationErrorOccurs_redisplayMedicineRegistrationPage() {
            //given:
            val invalidName = ""

            //when:
            val actions = mockMvc.perform(post(PATH)
                                              .with(csrf())
                                              .param("name", invalidName)
                                              .param("quantity", quantity.toString())
                                              .param("takingUnit", takingUnit)
                                              .param("timesPerDay", timesPerDay.toString())
                                              .param("timingOptions", timingOptions[0].name)
                                              .param("effects", effects[0])
                                              .param("effects", effects[1])
                                              .param("effects", effects[2])
                                              .param("precautions", precautions))

            //then:
            actions.andExpect(status().isOk)
                .andExpect(view().name("medicine/form"))
        }

        @Test
        @DisplayName("未認証ユーザによるリクエストの場合、トップページ画面にリダイレクトする")
        fun requestedByUnauthenticatedUser_redirectToToppagePage() {
            //when:
            val actions = mockMvc.perform(post(PATH)
                                              .with(csrf())
                                              .param("name", name)
                                              .param("quantity", quantity.toString())
                                              .param("takingUnit", takingUnit)
                                              .param("timesPerDay", timesPerDay.toString())
                                              .param("timingOptions", timingOptions[0].name)
                                              .param("effects", effects[0])
                                              .param("effects", effects[1])
                                              .param("effects", effects[2])
                                              .param("precautions", precautions))

            actions.andExpect(status().isFound)
                .andExpect(redirectedUrl("/"))
        }
    }
}