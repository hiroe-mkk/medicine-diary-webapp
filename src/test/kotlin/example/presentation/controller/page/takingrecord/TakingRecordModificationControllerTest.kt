package example.presentation.controller.page.takingrecord

import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import example.presentation.controller.api.takingrecord.*
import example.presentation.shared.session.*
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
internal class TakingRecordModificationControllerTest(@Autowired private val mockMvc: MockMvc,
                                                      @Autowired private val testMedicineInserter: TestMedicineInserter,
                                                      @Autowired private val testTakingRecordInserter: TestTakingRecordInserter,
                                                      @Autowired private val userSessionProvider: UserSessionProvider) {
    companion object {
        private const val PATH = "/takingrecords/{takingRecordId}/modify"
    }

    @Nested
    inner class DisplayTakingRecordModificationPageTest {
        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("服用記録修正画面を表示する")
        fun displayTakingRecordModificationPage() {
            //given:
            val userSession = userSessionProvider.getUserSession()
            val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))
            val takingRecord = testTakingRecordInserter.insert(userSession.accountId, medicine.id)

            //when:
            val actions = mockMvc.perform(get(PATH, takingRecord.id))

            //then:
            actions.andExpect(status().isOk)
                .andExpect(view().name("takingrecord/form"))
        }

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("服用記録が見つからなかった場合、NotFoundエラー画面を表示する")
        fun takingRecordNotFound_displayNotFoundErrorPage() {
            //then:
            val badTakingRecordId = TakingRecordId("NonexistentId")

            //when:
            val actions = mockMvc.perform(get(PATH, badTakingRecordId))

            //then:
            actions.andExpect(status().isNotFound)
        }

        @Test
        @DisplayName("未認証ユーザによるリクエストの場合、トップページ画面へリダイレクトする")
        fun requestedByUnauthenticatedUser_redirectsToToppagePage() {
            //given:
            val takingRecordId = TakingRecordId("takingRecordId")

            //when:
            val actions = mockMvc.perform(get(PATH, takingRecordId))

            //then:
            actions.andExpect(status().isFound)
                .andExpect(redirectedUrl("/"))
        }
    }

    @Nested
    inner class ModifyTakingRecordTest {
        private val quantity: Double? = 2.0
        private val symptom: String = "頭痛"
        private val beforeTaking: ConditionLevel = ConditionLevel.A_LITTLE_BAD
        private val afterTaking: ConditionLevel = ConditionLevel.GOOD
        private val note: String = "それほど酷い頭痛ではなかったけれど、早めに飲んでおいたらいつもより早めに治った気がする。"
        private val takenAt: String = "2000-01-01T10:00"

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("服用記録の修正に成功した場合、最後にリクエストされた画面にリダイレクトする")
        fun takingRecordModificationSucceeds_redirectToLastRequestedPage() {
            //given:
            val userSession = userSessionProvider.getUserSession()
            val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))
            val takingRecord = testTakingRecordInserter.insert(userSession.accountId, medicine.id)

            //when:
            val actions = mockMvc.perform(post(PATH, takingRecord.id)
                                              .sessionAttr("lastRequestedPagePath", LastRequestedPagePath("/medicine"))
                                              .with(csrf())
                                              .param("takenMedicine", medicine.id.value)
                                              .param("quantity", quantity.toString())
                                              .param("symptom", symptom)
                                              .param("beforeTaking", beforeTaking?.name)
                                              .param("afterTaking", afterTaking?.name)
                                              .param("note", note)
                                              .param("takenAt", takenAt))

            //then:
            actions.andExpect(status().isFound)
                .andExpect(redirectedUrl("/medicine"))
        }

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("バリデーションエラーが発生した場合、薬登録画面を再表示する")
        fun validationErrorOccurs_redisplayTakingRecordModificationPage() {
            //given:
            val takingRecordId = TakingRecordId("takingRecordId")
            val invalidMedicineId = ""

            //when:
            val actions = mockMvc.perform(post(PATH, takingRecordId)
                                              .with(csrf())
                                              .param("takenMedicine", invalidMedicineId)
                                              .param("quantity", quantity.toString())
                                              .param("symptom", symptom)
                                              .param("beforeTaking", beforeTaking?.name)
                                              .param("afterTaking", afterTaking?.name)
                                              .param("note", note)
                                              .param("takenAt", takenAt))

            //then:
            actions.andExpect(status().isOk)
                .andExpect(view().name("takingrecord/form"))
        }

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("服用記録が見つからなかった場合、NotFoundエラー画面を表示する")
        fun takingRecordNotFound_displayNotFoundErrorPage() {
            //given:
            val userSession = userSessionProvider.getUserSession()
            val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))
            val badTakingRecordId = TakingRecordId("NonexistentId")

            //when:
            val actions = mockMvc.perform(post(PATH, badTakingRecordId)
                                              .with(csrf())
                                              .param("takenMedicine", medicine.id.value)
                                              .param("quantity", quantity.toString())
                                              .param("symptom", symptom)
                                              .param("beforeTaking", beforeTaking?.name)
                                              .param("afterTaking", afterTaking?.name)
                                              .param("note", note)
                                              .param("takenAt", takenAt))

            //then:
            actions.andExpect(status().isNotFound)
        }

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("薬が見つからなかった場合、最後にリクエストされた画面にリダイレクトする")
        fun medicineNotFound_redirectToLastRequestedPage() {
            //given:
            val takingRecordId = TakingRecordId("takingRecordId")
            val badMedicineId = "NonexistentId"

            //when:
            val actions = mockMvc.perform(post(PATH, takingRecordId)
                                              .sessionAttr("lastRequestedPagePath", LastRequestedPagePath("/medicine"))
                                              .with(csrf())
                                              .param("takenMedicine", badMedicineId)
                                              .param("quantity", quantity.toString())
                                              .param("symptom", symptom)
                                              .param("beforeTaking", beforeTaking?.name)
                                              .param("afterTaking", afterTaking?.name)
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
            val takingRecordId = TakingRecordId("takingRecordId")

            //when:
            val actions = mockMvc.perform(post(PATH, takingRecordId)
                                              .with(csrf())
                                              .param("takenMedicine", "medicineId")
                                              .param("quantity", quantity.toString())
                                              .param("symptom", symptom)
                                              .param("beforeTaking", beforeTaking?.name)
                                              .param("afterTaking", afterTaking?.name)
                                              .param("note", note)
                                              .param("takenAt", takenAt))

            actions.andExpect(status().isFound)
                .andExpect(redirectedUrl("/"))
        }
    }
}