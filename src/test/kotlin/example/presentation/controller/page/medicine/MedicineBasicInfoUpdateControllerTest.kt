package example.presentation.controller.page.medicine

import example.domain.model.medicine.*
import example.presentation.shared.usersession.*
import example.testhelper.inserter.*
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
internal class MedicineBasicInfoUpdateControllerTest(@Autowired private val mockMvc: MockMvc,
                                                     @Autowired private val testMedicineInserter: TestMedicineInserter,
                                                     @Autowired private val userSessionProvider: UserSessionProvider) {
    companion object {
        private const val PATH = "/medicines/{medicineId}/basicinfo/update"
    }

    @Nested
    inner class DisplayMedicineBasicInfoUpdatePageTest {
        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("薬基本情報更新画面を表示する")
        fun displayMedicineBasicInfoUpdatePage() {
            //given:
            val userSession = userSessionProvider.getUserSession()
            val medicine = testMedicineInserter.insert(userSession.accountId)

            //when:
            val actions = mockMvc.perform(get(PATH, medicine.id))

            //then:
            actions.andExpect(status().isOk)
                .andExpect(view().name("medicine/basicInfoForm"))
        }

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("薬が見つからなかった場合、NotFoundエラー画面を表示する")
        fun medicineNotFound_displayNotFoundErrorPage() {
            //given:
            val badMedicineId = MedicineId("NonexistentId")

            //when:
            val actions = mockMvc.perform(get(PATH, badMedicineId))

            //then:
            actions.andExpect(status().isNotFound)
                .andExpect(view().name("error/notFoundError"))
        }

        @Test
        @DisplayName("未認証ユーザによるリクエストの場合、トップページ画面へリダイレクトする")
        fun requestedByUnauthenticatedUser_redirectsToToppagePage() {
            //given:
            val medicineId = MedicineId("medicineId")

            //when:
            val actions = mockMvc.perform(get(PATH, medicineId))

            //then:
            actions.andExpect(status().isFound)
                .andExpect(redirectedUrl("/"))
        }
    }

    @Nested
    inner class UpdateMedicineBasicInfoTest {
        private val name = "ロキソニンS"
        private val takingUnit = "錠"
        private val quantity = 1.0
        private val timesPerDay = 2
        private val effects = arrayOf("頭痛", "解熱", "肩こり", "歯痛")
        private val precautions = "再度症状があらわれた場合には3回目を服用できます。"

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("薬基本情報の更新に成功した場合、薬詳細画面にリダイレクトする")
        fun medicineBasicInfoUpdateSucceeds_redirectToMedicineDetailPage() {
            //given:
            val userSession = userSessionProvider.getUserSession()
            val medicine = testMedicineInserter.insert(userSession.accountId)

            //when:
            val actions = mockMvc.perform(post(PATH, medicine.id)
                                              .with(csrf())
                                              .param("name", name)
                                              .param("quantity", quantity.toString())
                                              .param("takingUnit", takingUnit)
                                              .param("timesPerDay", timesPerDay.toString())
                                              .param("effects", effects[0])
                                              .param("effects", effects[1])
                                              .param("effects", effects[2])
                                              .param("effects", effects[3])
                                              .param("precautions", precautions))

            //then:
            actions.andExpect(status().isFound)
                .andExpect(redirectedUrl("/medicines/${medicine.id}"))
        }

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("バリデーションエラーが発生した場合、薬基本情報更新画面を再表示する")
        fun validationErrorOccurs_redisplayMedicineBasicInfoUpdatePage() {
            //given:
            val userSession = userSessionProvider.getUserSession()
            val medicine = testMedicineInserter.insert(userSession.accountId)
            val invalidName = ""

            //when:
            val actions = mockMvc.perform(post(PATH, medicine.id)
                                              .with(csrf())
                                              .param("name", invalidName)
                                              .param("quantity", quantity.toString())
                                              .param("takingUnit", takingUnit)
                                              .param("timesPerDay", timesPerDay.toString())
                                              .param("effects", effects[0])
                                              .param("effects", effects[1])
                                              .param("effects", effects[2])
                                              .param("effects", effects[3])
                                              .param("precautions", precautions))

            //then:
            actions.andExpect(status().isOk)
                .andExpect(view().name("medicine/basicInfoForm"))
        }

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("薬が見つからなかった場合、NotFoundエラー画面を表示する")
        fun medicineNotFound_displayNotFoundErrorPage() {
            //given:
            val badMedicineId = MedicineId("NonexistentId")

            //when:
            val actions = mockMvc.perform(post(PATH, badMedicineId)
                                              .with(csrf())
                                              .param("name", name)
                                              .param("quantity", quantity.toString())
                                              .param("takingUnit", takingUnit)
                                              .param("timesPerDay", timesPerDay.toString())
                                              .param("effects", effects[0])
                                              .param("effects", effects[1])
                                              .param("effects", effects[2])
                                              .param("effects", effects[3])
                                              .param("precautions", precautions))

            //then:
            actions.andExpect(status().isNotFound)
                .andExpect(view().name("error/notFoundError"))
        }

        @Test
        @DisplayName("未認証ユーザによるリクエストの場合、トップページ画面にリダイレクトする")
        fun requestedByUnauthenticatedUser_redirectToToppagePage() {
            //given:
            val medicineId = MedicineId("medicineId")

            //when:
            val actions = mockMvc.perform(post(PATH, medicineId)
                                              .with(csrf())
                                              .param("name", name)
                                              .param("quantity", quantity.toString())
                                              .param("takingUnit", takingUnit)
                                              .param("timesPerDay", timesPerDay.toString())
                                              .param("effects", effects[0])
                                              .param("effects", effects[1])
                                              .param("effects", effects[2])
                                              .param("effects", effects[3])
                                              .param("precautions", precautions))

            actions.andExpect(status().isFound)
                .andExpect(redirectedUrl("/"))
        }
    }
}