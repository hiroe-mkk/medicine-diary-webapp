package example.presentation.controller.page.medicine

import example.domain.model.medicine.*
import example.presentation.shared.usersession.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import example.testhelper.springframework.security.*
import org.assertj.core.api.InstanceOfAssertFactories.*
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

@ControllerTest
internal class MedicineDeletionControllerTest(@Autowired private val mockMvc: MockMvc,
                                              @Autowired private val testMedicineInserter: TestMedicineInserter,
                                              @Autowired private val userSessionProvider: UserSessionProvider) {
    companion object {
        private const val PATH = "/medicines/{medicineId}/delete"
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("薬の削除に成功した場合、薬概要一覧画面にリダイレクトする")
    fun medicineUpdateSucceeds_redirectToMedicineDetailPage() {
        //given:
        val userSession = userSessionProvider.getUserSession()
        val medicine = testMedicineInserter.insert(userSession.accountId)

        //when:
        val actions = mockMvc.perform(post(PATH, medicine.id)
                                          .with(csrf()))

        //then:
        actions.andExpect(status().isFound)
            .andExpect(redirectedUrl("/medicines"))
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("薬が見つからなかった場合、NotFoundエラー画面を表示する")
    fun medicineDetailNotFound_displayNotFoundErrorPage() {
        //given:
        val badMedicineId = MedicineId("NonexistentId")

        //when:
        val actions = mockMvc.perform(post(PATH, badMedicineId)
                                          .with(csrf()))

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
                                          .with(csrf()))

        actions.andExpect(status().isFound)
            .andExpect(redirectedUrl("/"))
    }
}