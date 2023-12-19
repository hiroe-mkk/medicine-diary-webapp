package example.presentation.controller.page.medicine

import example.domain.model.account.*
import example.domain.model.medicine.*
import example.testhelper.springframework.autoconfigure.*
import example.testhelper.springframework.security.*
import io.mockk.*
import org.junit.jupiter.api.*
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.test.mock.mockito.*
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
        @DisplayName("未認証ユーザによるリクエストの場合、ホーム画面へリダイレクトする")
        fun requestedByUnauthenticatedUser_redirectsToHomePage() {
            //when:
            val actions = mockMvc.perform(get(PATH))

            //then:
            actions.andExpect(status().isFound)
                .andExpect(redirectedUrl("/"))
        }
    }

    @Nested
    inner class RegisterMedicineTest {
        private val medicineName = "ロキソニンS"
        private val doseUnit = "錠"
        private val quantity = 1.0
        private val timesPerDay = 3
        private val effects = arrayOf("頭痛", "解熱")
        private val precautions = "服用間隔は4時間以上開けること。"
        private val isPublic = "true"
        private val isOwnedBySharedGroup = "false"

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("薬の登録に成功した場合、薬詳細画面にリダイレクトする")
        fun medicineRegistrationSucceeds_redirectToMedicineOverviewsPage() {
            //when:
            val actions = mockMvc.perform(post(PATH)
                                              .with(csrf())
                                              .param("medicineName", medicineName)
                                              .param("quantity", quantity.toString())
                                              .param("doseUnit", doseUnit)
                                              .param("timesPerDay", timesPerDay.toString())
                                              .param("effects", effects[0])
                                              .param("effects", effects[1])
                                              .param("precautions", precautions)
                                              .param("isPublic", isPublic)
                                              .param("isOwnedBySharedGroup", isOwnedBySharedGroup))

            //then:
            val medicineId = actions.andReturn().modelAndView?.model?.get("medicineId");
            actions.andExpect(status().isFound)
                .andExpect(redirectedUrl("/medicines/${medicineId}"))
        }

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("バリデーションエラーが発生した場合、薬登録画面を再表示する")
        fun validationErrorOccurs_redisplayMedicineRegistrationPage() {
            //given:
            val invalidMedicineName = ""

            //when:
            val actions = mockMvc.perform(post(PATH)
                                              .with(csrf())
                                              .param("medicineName", invalidMedicineName)
                                              .param("quantity", quantity.toString())
                                              .param("doseUnit", doseUnit)
                                              .param("timesPerDay", timesPerDay.toString())
                                              .param("effects", effects[0])
                                              .param("effects", effects[1])
                                              .param("precautions", precautions)
                                              .param("isPublic", isPublic)
                                              .param("isOwnedBySharedGroup", isOwnedBySharedGroup))

            //then:
            actions.andExpect(status().isOk)
                .andExpect(view().name("medicine/form"))
        }

        @Test
        @DisplayName("未認証ユーザによるリクエストの場合、ホーム画面にリダイレクトする")
        fun requestedByUnauthenticatedUser_redirectToHomePage() {
            //when:
            val actions = mockMvc.perform(post(PATH)
                                              .with(csrf())
                                              .param("medicineName", medicineName)
                                              .param("quantity", quantity.toString())
                                              .param("doseUnit", doseUnit)
                                              .param("timesPerDay", timesPerDay.toString())
                                              .param("effects", effects[0])
                                              .param("effects", effects[1])
                                              .param("precautions", precautions)
                                              .param("isPublic", isPublic)
                                              .param("isOwnedBySharedGroup", isOwnedBySharedGroup))

            //then
            actions.andExpect(status().isFound)
                .andExpect(redirectedUrl("/"))
        }
    }
}