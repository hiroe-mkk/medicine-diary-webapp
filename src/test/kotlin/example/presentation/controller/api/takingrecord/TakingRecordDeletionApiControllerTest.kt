package example.presentation.controller.api.takingrecord

import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
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
internal class TakingRecordDeletionApiControllerTest(@Autowired private val mockMvc: MockMvc,
                                                     @Autowired private val testMedicineInserter: TestMedicineInserter,
                                                     @Autowired private val testTakingRecordInserter: TestTakingRecordInserter,
                                                     @Autowired private val userSessionProvider: UserSessionProvider) {
    companion object {
        private const val PATH = "/api/takingrecords/{takingRecordId}/delete"
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("服用記録を削除する")
    fun deleteTakingRecord() {
        //given:
        val userSession = userSessionProvider.getUserSession()
        val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))
        val takingRecord = testTakingRecordInserter.insert(userSession.accountId, medicine.id)

        //when:
        val actions = mockMvc.perform(post(PATH, takingRecord.id)
                                          .with(csrf()))

        //then:
        actions.andExpect(status().isNoContent)
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("服用記録が見つからなかった場合、ステータスコード404のレスポンスを返す")
    fun takingRecordNotFound_returnsResponseWithStatus404() {
        //then:
        val badTakingRecordId = TakingRecordId("NonexistentId")

        //when:
        val actions = mockMvc.perform(post(PATH, badTakingRecordId)
                                          .with(csrf()))

        //then:
        actions.andExpect(status().isNotFound)
    }


    @Test
    @DisplayName("未認証ユーザによるリクエストの場合、ステータスコード401のレスポンスを返す")
    fun requestedByUnauthenticatedUser_returnsResponseWithStatus401() {
        //given:
        val takingRecordId = TakingRecordId("takingRecordId")

        //when:
        val actions = mockMvc.perform(post(PATH, takingRecordId)
                                          .with(csrf()))

        //then:
        actions.andExpect(status().isUnauthorized)
    }
}