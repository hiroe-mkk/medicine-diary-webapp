package example.presentation.controller.page.medicine

import example.domain.model.medicine.*
import example.infrastructure.repository.shared.*
import example.presentation.shared.usersession.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import example.testhelper.springframework.security.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
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
            val userSession = userSessionProvider.getUserSessionOrElseThrow()
            val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))

            //when:
            val actions = mockMvc.perform(get(PATH, medicine.id))

            //then:
            actions.andExpect(status().isOk)
                .andExpect(view().name("medicine/form"))
        }

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("薬が見つからなかった場合、NotFoundエラー画面を表示する")
        fun medicineNotFound_displayNotFoundErrorPage() {
            //given:
            val nonexistentMedicineId = MedicineId(EntityIdHelper.generate())

            //when:
            val actions = mockMvc.perform(get(PATH, nonexistentMedicineId))

            //then:
            actions.andExpect(status().isNotFound)
                .andExpect(view().name("error/notFoundError"))
        }

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("無効な形式の薬IDの場合、NotFoundエラー画面を表示する")
        fun invalidMedicineId_displayNotFoundErrorPage() {
            //given:
            val invalidMedicineId = MedicineId("invalidMedicineId")

            //when:
            val actions = mockMvc.perform(get(PATH, invalidMedicineId))

            //then:
            actions.andExpect(status().isBadRequest)
                .andExpect(view().name("error/notFoundError"))
        }

        @Test
        @DisplayName("未認証ユーザによるリクエストの場合、ホーム画面へリダイレクトする")
        fun requestedByUnauthenticatedUser_redirectsToHomePage() {
            //given:
            val medicineId = MedicineId(EntityIdHelper.generate())

            //when:
            val actions = mockMvc.perform(get(PATH, medicineId))

            //then:
            actions.andExpect(status().isFound)
                .andExpect(redirectedUrl("/"))
        }
    }

    @Nested
    inner class UpdateMedicineBasicInfoTest {
        private val medicineName = "ロキソニンSプレミアム"
        private val doseUnit = "錠"
        private val quantity = 2.0
        private val timesPerDay = 3
        private val timingOptions = listOf(Timing.AS_NEEDED)
        private val effects = arrayOf("頭痛", "解熱", "肩こり")
        private val precautions = "服用間隔は4時間以上開けること。\n再度症状があらわれた場合には3回目を服用してもよい。"

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("薬基本情報の更新に成功した場合、薬詳細画面にリダイレクトする")
        fun medicineBasicInfoUpdateSucceeds_redirectToMedicineDetailPage() {
            //given:
            val userSession = userSessionProvider.getUserSessionOrElseThrow()
            val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))

            //when:
            val actions = mockMvc.perform(post(PATH, medicine.id)
                                              .with(csrf())
                                              .param("medicineName", medicineName)
                                              .param("quantity", quantity.toString())
                                              .param("doseUnit", doseUnit)
                                              .param("timesPerDay", timesPerDay.toString())
                                              .param("timingOptions", timingOptions[0].name)
                                              .param("effects", effects[0])
                                              .param("effects", effects[1])
                                              .param("effects", effects[2])
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
            val userSession = userSessionProvider.getUserSessionOrElseThrow()
            val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))
            val invalidMedicineName = ""

            //when:
            val actions = mockMvc.perform(post(PATH, medicine.id)
                                              .with(csrf())
                                              .param("medicineName", invalidMedicineName)
                                              .param("quantity", quantity.toString())
                                              .param("doseUnit", doseUnit)
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
        @WithMockAuthenticatedAccount
        @DisplayName("薬が見つからなかった場合、NotFoundエラー画面を表示する")
        fun medicineNotFound_displayNotFoundErrorPage() {
            //given:
            val nonexistentMedicineId = MedicineId(EntityIdHelper.generate())

            //when:
            val actions = mockMvc.perform(post(PATH, nonexistentMedicineId)
                                              .with(csrf())
                                              .param("medicineName", medicineName)
                                              .param("quantity", quantity.toString())
                                              .param("doseUnit", doseUnit)
                                              .param("timesPerDay", timesPerDay.toString())
                                              .param("timingOptions", timingOptions[0].name)
                                              .param("effects", effects[0])
                                              .param("effects", effects[1])
                                              .param("effects", effects[2])
                                              .param("precautions", precautions))

            //then:
            actions.andExpect(status().isNotFound)
                .andExpect(view().name("error/notFoundError"))
        }

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("無効な形式の薬IDの場合、NotFoundエラー画面を表示する")
        fun invalidMedicineId_displayNotFoundErrorPage() {
            //given:
            val invalidMedicineId = MedicineId("invalidMedicineId")

            //when:
            val actions = mockMvc.perform(post(PATH, invalidMedicineId)
                                              .with(csrf())
                                              .param("medicineName", medicineName)
                                              .param("quantity", quantity.toString())
                                              .param("doseUnit", doseUnit)
                                              .param("timesPerDay", timesPerDay.toString())
                                              .param("timingOptions", timingOptions[0].name)
                                              .param("effects", effects[0])
                                              .param("effects", effects[1])
                                              .param("effects", effects[2])
                                              .param("precautions", precautions))

            //then:
            actions.andExpect(status().isBadRequest)
                .andExpect(view().name("error/notFoundError"))
        }

        @Test
        @DisplayName("未認証ユーザによるリクエストの場合、ホーム画面にリダイレクトする")
        fun requestedByUnauthenticatedUser_redirectToHomePage() {
            //given:
            val medicineId = MedicineId(EntityIdHelper.generate())

            //when:
            val actions = mockMvc.perform(post(PATH, medicineId)
                                              .with(csrf())
                                              .param("medicineName", medicineName)
                                              .param("quantity", quantity.toString())
                                              .param("doseUnit", doseUnit)
                                              .param("timesPerDay", timesPerDay.toString())
                                              .param("timingOptions", timingOptions[0].name)
                                              .param("effects", effects[0])
                                              .param("effects", effects[1])
                                              .param("effects", effects[2])
                                              .param("precautions", precautions))

            //then:
            actions.andExpect(status().isFound)
                .andExpect(redirectedUrl("/"))
        }
    }
}