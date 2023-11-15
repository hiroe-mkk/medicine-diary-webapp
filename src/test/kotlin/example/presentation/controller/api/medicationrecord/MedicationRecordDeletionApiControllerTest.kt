package example.presentation.controller.api.medicationrecord

import example.domain.model.medicine.*
import example.domain.model.medicationrecord.*
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
internal class MedicationRecordDeletionApiControllerTest(@Autowired private val mockMvc: MockMvc,
                                                         @Autowired private val testMedicineInserter: TestMedicineInserter,
                                                         @Autowired private val testMedicationRecordInserter: TestMedicationRecordInserter,
                                                         @Autowired private val userSessionProvider: UserSessionProvider) {
    companion object {
        private const val PATH = "/api/medication-records/{medicationRecordId}/delete"
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("服用記録を削除する")
    fun deleteMedicationRecord() {
        //given:
        val userSession = userSessionProvider.getUserSession()
        val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))
        val medicationRecord = testMedicationRecordInserter.insert(userSession.accountId, medicine.id)

        //when:
        val actions = mockMvc.perform(post(PATH, medicationRecord.id)
                                          .with(csrf()))

        //then:
        actions.andExpect(status().isNoContent)
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("服用記録が見つからなかった場合、ステータスコード204のレスポンスを返す")
    fun medicationRecordNotFound_returnsResponseWithStatus404() {
        //then:
        val badMedicationRecordId = MedicationRecordId("NonexistentId")

        //when:
        val actions = mockMvc.perform(post(PATH, badMedicationRecordId)
                                          .with(csrf()))

        //then:
        actions.andExpect(status().isNoContent)
    }

    @Test
    @DisplayName("未認証ユーザによるリクエストの場合、ステータスコード401のレスポンスを返す")
    fun requestedByUnauthenticatedUser_returnsResponseWithStatus401() {
        //given:
        val medicationRecordId = MedicationRecordId("medicationRecordId")

        //when:
        val actions = mockMvc.perform(post(PATH, medicationRecordId)
                                          .with(csrf()))

        //then:
        actions.andExpect(status().isUnauthorized)
    }
}