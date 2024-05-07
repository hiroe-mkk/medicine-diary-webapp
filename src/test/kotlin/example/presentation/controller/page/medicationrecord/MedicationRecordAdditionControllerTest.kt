package example.presentation.controller.page.medicationrecord

import example.domain.model.medicationrecord.*
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
            val userSession = userSessionProvider.getUserSessionOrElseThrow()
            testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))

            //when:
            val actions = mockMvc.perform(get(PATH).param("date", "2020-01-01"))

            //then:
            actions.andExpect(status().isOk)
                .andExpect(view().name("medicationrecord/form"))
        }

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("バリデーションエラーが発生した場合、ステータスコード400のレスポンスを返す")
        fun validationErrorOccurs_returnsResponseWithStatus400() {
            //when:
            val actions = mockMvc.perform(get(PATH)
                                              .param("medicine", "invalidMedicineId"))

            //then:
            actions.andExpect(status().isBadRequest)
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
    inner class AddMedicationRecordTest {
        private val quantity: Double = 1.0
        private val symptom: String = "頭痛"
        private val beforeMedication: ConditionLevel = ConditionLevel.A_LITTLE_BAD
        private val note: String = ""
        private val takenMedicineOn: String = "2020-01-01"
        private val takenMedicineAt: String = "07:00"
        private val symptomOnsetAt: String = "06:30"

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("服用記録の追加に成功した場合、直前に閲覧していた画面にリダイレクトする")
        fun medicationRecordAdditionSucceeds_redirectToLastRequestedPage() {
            //given:
            val userSession = userSessionProvider.getUserSessionOrElseThrow()
            val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))
            mockMvc.perform(get("/medicines"))
                .andExpect(status().isOk())
                .andReturn()

            //when:
            val actions = mockMvc.perform(post(PATH)
                                              .with(csrf())
                                              .param("takenMedicine", medicine.id.value)
                                              .param("quantity", quantity.toString())
                                              .param("symptom", symptom)
                                              .param("beforeMedication", beforeMedication.name)
                                              .param("note", note)
                                              .param("takenMedicineOn", takenMedicineOn)
                                              .param("takenMedicineAt", takenMedicineAt)
                                              .param("symptomOnsetAt", symptomOnsetAt))

            //then:
            actions.andExpect(status().isFound)
                .andExpect(redirectedUrl("/medicines"))
        }

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("バリデーションエラーが発生した場合、服用記録追加画面を再表示する")
        fun validationErrorOccurs_redisplayMedicationRecordAdditionPage() {
            //given:
            val userSession = userSessionProvider.getUserSessionOrElseThrow()
            val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))
            val invalidQuantity = -1

            //when:
            val actions = mockMvc.perform(post(PATH)
                                              .with(csrf())
                                              .param("takenMedicine", medicine.id.value)
                                              .param("quantity", invalidQuantity.toString())
                                              .param("symptom", symptom)
                                              .param("beforeMedication", beforeMedication.name)
                                              .param("note", note)
                                              .param("takenMedicineOn", takenMedicineOn)
                                              .param("takenMedicineAt", takenMedicineAt)
                                              .param("symptomOnsetAt", symptomOnsetAt))
            //then:
            actions.andExpect(status().isOk)
                .andExpect(view().name("medicationrecord/form"))
        }

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("薬が見つからなかった場合、NotFoundエラー画面を表示する")
        fun medicineNotFound_redirectToLastRequestedPage() {
            //given:
            val nonexistentMedicineId = MedicineId(EntityIdHelper.generate())
            mockMvc.perform(get("/medicines"))
                .andExpect(status().isOk())
                .andReturn()

            //when:
            val actions = mockMvc.perform(post(PATH)
                                              .with(csrf())
                                              .param("takenMedicine", nonexistentMedicineId.toString())
                                              .param("quantity", quantity.toString())
                                              .param("symptom", symptom)
                                              .param("beforeMedication", beforeMedication.name)
                                              .param("note", note)
                                              .param("takenMedicineOn", takenMedicineOn)
                                              .param("takenMedicineAt", takenMedicineAt)
                                              .param("symptomOnsetAt", symptomOnsetAt))

            //then:
            actions.andExpect(status().isNotFound)
                .andExpect(view().name("error/notFoundError"))
        }

        @Test
        @DisplayName("未認証ユーザによるリクエストの場合、ホーム画面にリダイレクトする")
        fun requestedByUnauthenticatedUser_redirectToHomePage() {
            //given:
            val medicineId = MedicineId(EntityIdHelper.generate())

            //when:
            val actions = mockMvc.perform(post(PATH)
                                              .with(csrf())
                                              .param("takenMedicine", medicineId.toString())
                                              .param("quantity", quantity.toString())
                                              .param("symptom", symptom)
                                              .param("beforeMedication", beforeMedication.name)
                                              .param("note", note)
                                              .param("takenMedicineOn", takenMedicineOn)
                                              .param("takenMedicineAt", takenMedicineAt)
                                              .param("symptomOnsetAt", symptomOnsetAt))

            //then:
            actions.andExpect(status().isFound)
                .andExpect(redirectedUrl("/"))
        }
    }
}