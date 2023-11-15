package example.presentation.controller.page.medicationrecord

import example.application.service.medicationrecord.*
import example.application.service.medicationrecord.MedicationRecordEditCommand.*
import example.domain.model.medicine.*
import example.domain.model.medicationrecord.*
import example.presentation.controller.page.medicine.*
import example.presentation.shared.session.*
import example.presentation.shared.usersession.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import example.testhelper.springframework.security.*
import org.bouncycastle.asn1.x500.style.RFC4519Style.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.security.test.web.servlet.request.*
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.*

@ControllerTest
internal class MedicationRecordAdditionControllerTest(@Autowired private val mockMvc: MockMvc,
                                                      @Autowired private val testMedicineInserter: TestMedicineInserter,
                                                      @Autowired private val userSessionProvider: UserSessionProvider) {
    companion object {
        private const val PATH = "/medication-records/add"
    }

    @Nested
    inner class DisplayMedicationRecordAdditionPageTest {
        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("服用記録追加画面を表示する")
        fun displayMedicationRecordAdditionPage() {
            //given:
            val userSession = userSessionProvider.getUserSession()
            testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))

            //when:
            val actions = mockMvc.perform(get(PATH))

            //then:
            actions.andExpect(status().isOk)
                .andExpect(view().name("medicationrecord/form"))
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
    inner class AddMedicationRecordTest {
        private val quantity: Double? = 1.0
        private val symptom: String = "頭痛"
        private val beforeTaking: ConditionLevel = ConditionLevel.A_LITTLE_BAD
        private val note: String = ""
        private val takenAt: String = "2000-01-01T07:00"

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("服用記録の追加に成功した場合、最後にリクエストされた画面にリダイレクトする")
        fun medicationRecordAdditionSucceeds_redirectToLastRequestedPage() {
            //given:
            val userSession = userSessionProvider.getUserSession()
            val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))

            //when:
            val actions = mockMvc.perform(post(PATH)
                                              .sessionAttr("lastRequestedPagePath", LastRequestedPagePath("/medicine"))
                                              .with(csrf())
                                              .param("takenMedicine", medicine.id.value)
                                              .param("quantity", quantity.toString())
                                              .param("symptom", symptom)
                                              .param("beforeTaking", beforeTaking?.name)
                                              .param("note", note)
                                              .param("takenAt", takenAt))

            //then:
            actions.andExpect(status().isFound)
                .andExpect(redirectedUrl("/medicine"))
        }

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("バリデーションエラーが発生した場合、薬登録画面を再表示する")
        fun validationErrorOccurs_redisplayMedicationRecordAdditionPage() {
            //given:
            val invalidMedicineId = ""

            //when:
            val actions = mockMvc.perform(post(PATH)
                                              .with(csrf())
                                              .param("takenMedicine", invalidMedicineId)
                                              .param("quantity", quantity.toString())
                                              .param("symptom", symptom)
                                              .param("beforeTaking", beforeTaking?.name)
                                              .param("note", note)
                                              .param("takenAt", takenAt))

            //then:
            actions.andExpect(status().isOk)
                .andExpect(view().name("medicationrecord/form"))
        }

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("薬が見つからなかった場合、最後にリクエストされた画面にリダイレクトする")
        fun medicineNotFound_redirectToLastRequestedPage() {
            //given:
            val badMedicineId = "NonexistentId"

            //when:
            val actions = mockMvc.perform(post(PATH)
                                              .sessionAttr("lastRequestedPagePath", LastRequestedPagePath("/medicine"))
                                              .with(csrf())
                                              .param("takenMedicine", badMedicineId)
                                              .param("quantity", quantity.toString())
                                              .param("symptom", symptom)
                                              .param("beforeTaking", beforeTaking?.name)
                                              .param("note", note)
                                              .param("takenAt", takenAt))

            //then:
            actions.andExpect(status().isFound)
                .andExpect(redirectedUrl("/medicine"))
        }

        @Test
        @DisplayName("未認証ユーザによるリクエストの場合、トップページ画面にリダイレクトする")
        fun requestedByUnauthenticatedUser_redirectToToppagePage() {
            //given:
            val medicineId = "medicineId"

            //when:
            val actions = mockMvc.perform(post(PATH)
                                              .with(csrf())
                                              .param("takenMedicine", medicineId)
                                              .param("quantity", quantity.toString())
                                              .param("symptom", symptom)
                                              .param("beforeTaking", beforeTaking?.name)
                                              .param("note", note)
                                              .param("takenAt", takenAt))

            actions.andExpect(status().isFound)
                .andExpect(redirectedUrl("/"))
        }
    }
}