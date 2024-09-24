package example.presentation.controller.page.medicine

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
internal class MedicineDeletionControllerTest(@Autowired private val mockMvc: MockMvc,
                                              @Autowired private val testMedicineInserter: TestMedicineInserter,
                                              @Autowired private val userSessionProvider: UserSessionProvider) {
    companion object {
        private const val PATH = "/medicines/{medicineId}/delete"
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("薬の削除に成功した場合、薬一覧画面にリダイレクトする")
    fun medicineDeletionSucceeds_redirectToMedicineOverviewsPage() {
        //given:
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))

        //when:
        val actions = mockMvc.perform(post(PATH, medicine.id)
                                          .with(csrf()))

        //then:
        actions.andExpect(status().isFound)
            .andExpect(redirectedUrl("/medicines"))
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("薬が見つからなかった場合、お薬一覧画面にリダイレクトする")
    fun medicineNotFound_redirectToMedicineOverviewsPage() {
        //given:
        val nonexistentMedicineId = EntityIdHelper.generate()

        //when:
        val actions = mockMvc.perform(post(PATH, nonexistentMedicineId)
                                          .with(csrf()))

        //then:
        actions.andExpect(status().isFound)
            .andExpect(redirectedUrl("/medicines"))
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("無効な形式の薬IDの場合、NotFoundエラー画面を表示する")
    fun invalidMedicineId_displayNotFoundErrorPage() {
        //given:
        val invalidMedicineId = "invalidMedicineId"

        //when:
        val actions = mockMvc.perform(post(PATH, invalidMedicineId)
                                          .with(csrf()))

        //then:
        actions.andExpect(status().isBadRequest)
            .andExpect(view().name("error/notFoundError"))
    }

    @Test
    @DisplayName("未認証ユーザによるリクエストの場合、ホーム画面にリダイレクトする")
    fun requestedByUnauthenticatedUser_redirectToHomePage() {
        //given:
        val medicineId = EntityIdHelper.generate()

        //when:
        val actions = mockMvc.perform(post(PATH, medicineId)
                                          .with(csrf()))

        //then:
        actions.andExpect(status().isFound)
            .andExpect(redirectedUrl("/"))
    }
}
