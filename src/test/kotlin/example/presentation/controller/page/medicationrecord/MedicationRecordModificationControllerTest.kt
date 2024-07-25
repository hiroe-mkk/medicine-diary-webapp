package example.presentation.controller.page.medicationrecord

import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import example.infrastructure.db.repository.shared.*
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
internal class MedicationRecordModificationControllerTest(@Autowired private val mockMvc: MockMvc,
                                                          @Autowired private val testMedicineInserter: TestMedicineInserter,
                                                          @Autowired private val testMedicationRecordInserter: TestMedicationRecordInserter,
                                                          @Autowired private val userSessionProvider: UserSessionProvider) {
    companion object {
        private const val PATH = "/medication-records/{medicationRecordId}/modify"
    }

    @Nested
    inner class DisplayMedicationRecordModificationPageTest {
        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("服用記録修正画面を表示する")
        fun displayMedicationRecordModificationPage() {
            //given:
            val userSession = userSessionProvider.getUserSessionOrElseThrow()
            val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))
            val medicationRecord = testMedicationRecordInserter.insert(userSession.accountId, medicine.id)

            //when:
            val actions = mockMvc.perform(get(PATH, medicationRecord.id))

            //then:
            actions.andExpect(status().isOk)
                .andExpect(view().name("medicationrecord/form"))
        }

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("服用記録が見つからなかった場合、NotFoundエラー画面を表示する")
        fun medicationRecordNotFound_displayNotFoundErrorPage() {
            //then:
            val nonexistentMedicationRecordId = MedicationRecordId(EntityIdHelper.generate())

            //when:
            val actions = mockMvc.perform(get(PATH, nonexistentMedicationRecordId))

            //then:
            actions.andExpect(status().isNotFound)
                .andExpect(view().name("error/notFoundError"))
        }

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("無効な形式の服用記録IDの場合、NotFoundエラー画面を表示する")
        fun invalidMedicationRecordId_displayNotFoundErrorPage() {
            //given:
            val invalidMedicationRecordId = MedicationRecordId("invalidMedicationRecordId")

            //when:
            val actions = mockMvc.perform(get(PATH, invalidMedicationRecordId))

            //then:
            actions.andExpect(status().isBadRequest)
                .andExpect(view().name("error/notFoundError"))
        }

        @Test
        @DisplayName("未認証ユーザによるリクエストの場合、ホーム画面へリダイレクトする")
        fun requestedByUnauthenticatedUser_redirectsToHomePage() {
            //given:
            val medicationRecordId = MedicationRecordId(EntityIdHelper.generate())

            //when:
            val actions = mockMvc.perform(get(PATH, medicationRecordId))

            //then:
            actions.andExpect(status().isFound)
                .andExpect(redirectedUrl("/"))
        }
    }

    @Nested
    inner class ModifyMedicationRecordTest {
        private val quantity: Double = 2.0
        private val symptom: String = "頭痛"
        private val beforeMedication: ConditionLevel = ConditionLevel.A_LITTLE_BAD
        private val afterMedication: ConditionLevel = ConditionLevel.GOOD
        private val note: String =
                "それほど酷い頭痛ではなかったけれど、早めに飲んでおいたらいつもより早めに治った気がする。"
        private val takenMedicineOn: String = "2020-01-02"
        private val takenMedicineAt: String = "10:00"
        private val onsetEffectAt: String = "10:30"

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("服用記録の修正に成功した場合、最後にリクエストされた画面にリダイレクトする")
        fun medicationRecordModificationSucceeds_redirectToLastRequestedPage() {
            //given:
            val userSession = userSessionProvider.getUserSessionOrElseThrow()
            val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))
            val medicationRecord = testMedicationRecordInserter.insert(userSession.accountId, medicine.id)
            mockMvc.perform(get("/medicines"))
                .andExpect(status().isOk())
                .andReturn()

            //when:
            val actions = mockMvc.perform(post(PATH, medicationRecord.id)
                                              .with(csrf())
                                              .param("takenMedicine", medicine.id.value)
                                              .param("quantity", quantity.toString())
                                              .param("symptom", symptom)
                                              .param("beforeMedication", beforeMedication.name)
                                              .param("afterMedication", afterMedication.name)
                                              .param("note", note)
                                              .param("takenMedicineOn", takenMedicineOn)
                                              .param("takenMedicineAt", takenMedicineAt)
                                              .param("onsetEffectAt", onsetEffectAt))

            //then:
            actions.andExpect(status().isFound)
                .andExpect(redirectedUrl("/medicines"))
        }

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("バリデーションエラーが発生した場合、服用記録修正画面を再表示する")
        fun validationErrorOccurs_redisplayMedicationRecordModificationPage() {
            //given:
            val userSession = userSessionProvider.getUserSessionOrElseThrow()
            val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))
            val medicationRecord = testMedicationRecordInserter.insert(userSession.accountId, medicine.id)
            val invalidQuantity = -1

            //when:
            val actions = mockMvc.perform(post(PATH, medicationRecord.id)
                                              .with(csrf())
                                              .param("takenMedicine", medicine.id.value)
                                              .param("quantity", invalidQuantity.toString())
                                              .param("symptom", symptom)
                                              .param("beforeMedication", beforeMedication.name)
                                              .param("afterMedication", afterMedication.name)
                                              .param("note", note)
                                              .param("takenMedicineOn", takenMedicineOn)
                                              .param("takenMedicineAt", takenMedicineAt)
                                              .param("onsetEffectAt", onsetEffectAt))

            //then:
            actions.andExpect(status().isOk)
                .andExpect(view().name("medicationrecord/form"))
        }

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("服用記録が見つからなかった場合、NotFoundエラー画面を表示する")
        fun medicationRecordNotFound_displayNotFoundErrorPage() {
            //given:
            val userSession = userSessionProvider.getUserSessionOrElseThrow()
            val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))
            val nonexistentMedicationRecordId = MedicationRecordId(EntityIdHelper.generate())

            //when:
            val actions = mockMvc.perform(post(PATH, nonexistentMedicationRecordId)
                                              .with(csrf())
                                              .param("takenMedicine", medicine.id.value)
                                              .param("quantity", quantity.toString())
                                              .param("symptom", symptom)
                                              .param("beforeMedication", beforeMedication.name)
                                              .param("afterMedication", afterMedication.name)
                                              .param("note", note)
                                              .param("takenMedicineOn", takenMedicineOn)
                                              .param("takenMedicineAt", takenMedicineAt)
                                              .param("onsetEffectAt", onsetEffectAt))

            //then:
            actions.andExpect(status().isNotFound)
                .andExpect(view().name("error/notFoundError"))
        }

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("薬が見つからなかった場合、NotFoundエラー画面を表示する")
        fun medicineNotFound_redirectToLastRequestedPage() {
            //given:
            val medicationRecordId = MedicationRecordId(EntityIdHelper.generate())
            val nonexistentMedicineId = MedicineId(EntityIdHelper.generate())
            mockMvc.perform(get("/medicines"))
                .andExpect(status().isOk())
                .andReturn()

            //when:
            val actions = mockMvc.perform(post(PATH, medicationRecordId)
                                              .with(csrf())
                                              .param("takenMedicine", nonexistentMedicineId.toString())
                                              .param("quantity", quantity.toString())
                                              .param("symptom", symptom)
                                              .param("beforeMedication", beforeMedication.name)
                                              .param("afterMedication", afterMedication.name)
                                              .param("note", note)
                                              .param("takenMedicineOn", takenMedicineOn)
                                              .param("takenMedicineAt", takenMedicineAt)
                                              .param("onsetEffectAt", onsetEffectAt))

            //then:
            actions.andExpect(status().isNotFound)
                .andExpect(view().name("error/notFoundError"))
        }

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("無効な形式の服用記録IDの場合、NotFoundエラー画面を表示する")
        fun invalidMedicationRecordId_displayNotFoundErrorPage() {
            //given:
            val invalidMedicationRecordId = MedicationRecordId("invalidMedicationRecordId")
            val medicineId = MedicineId(EntityIdHelper.generate())

            //when:
            val actions = mockMvc.perform(get(PATH, invalidMedicationRecordId)
                                              .with(csrf())
                                              .param("takenMedicine", medicineId.value)
                                              .param("quantity", quantity.toString())
                                              .param("symptom", symptom)
                                              .param("beforeMedication", beforeMedication.name)
                                              .param("afterMedication", afterMedication.name)
                                              .param("note", note)
                                              .param("takenMedicineOn", takenMedicineOn)
                                              .param("takenMedicineAt", takenMedicineAt)
                                              .param("onsetEffectAt", onsetEffectAt))

            //then:
            actions.andExpect(status().isBadRequest)
                .andExpect(view().name("error/notFoundError"))
        }

        @Test
        @DisplayName("未認証ユーザによるリクエストの場合、ホーム画面にリダイレクトする")
        fun requestedByUnauthenticatedUser_redirectToHomePage() {
            //given:
            val medicineId = MedicineId(EntityIdHelper.generate())
            val medicationRecordId = MedicationRecordId(EntityIdHelper.generate())

            //when:
            val actions = mockMvc.perform(post(PATH, medicationRecordId)
                                              .with(csrf())
                                              .param("takenMedicine", medicineId.toString())
                                              .param("quantity", quantity.toString())
                                              .param("symptom", symptom)
                                              .param("beforeMedication", beforeMedication.name)
                                              .param("afterMedication", afterMedication.name)
                                              .param("note", note)
                                              .param("takenMedicineOn", takenMedicineOn)
                                              .param("takenMedicineAt", takenMedicineAt)
                                              .param("onsetEffectAt", onsetEffectAt))

            //then:
            actions.andExpect(status().isFound)
                .andExpect(redirectedUrl("/"))
        }
    }
}
