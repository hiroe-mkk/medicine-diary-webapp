package example.presentation.controller.page.medicine

import example.domain.model.medicine.*
import example.infrastructure.repository.shared.*
import example.presentation.shared.usersession.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import example.testhelper.springframework.security.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ControllerTest
internal class MedicineDetailPageControllerTest(@Autowired private val mockMvc: MockMvc,
                                                @Autowired private val userSessionProvider: UserSessionProvider,
                                                @Autowired private val testMedicineInserter: TestMedicineInserter) {
    companion object {
        private const val PATH = "/medicines/{medicineId}"
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("薬詳細画面を表示する")
    fun displayMedicineDetailPage() {
        //given:
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))

        //when:
        val actions = mockMvc.perform(get(PATH, medicine.id))

        //then:
        actions.andExpect(status().isOk)
            .andExpect(view().name("medicine/detail"))
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