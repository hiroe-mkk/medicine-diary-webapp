package example.presentation.controller.page.takingrecord

import example.domain.model.takingrecord.*
import example.presentation.controller.api.takingrecord.*
import example.presentation.shared.usersession.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import example.testhelper.springframework.security.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
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
            val medicine = testMedicineInserter.insert(userSession.accountId)
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
}