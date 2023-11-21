package example.presentation.controller.page.medicine

import example.domain.model.medicine.*
import example.presentation.shared.usersession.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import example.testhelper.springframework.security.*
import org.assertj.core.api.InstanceOfAssertFactories.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ControllerTest
internal class MedicineDetailControllerTest(@Autowired private val mockMvc: MockMvc,
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
        val userSession = userSessionProvider.getUserSession()
        val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))

        //when:
        val actions = mockMvc.perform(get(PATH, medicine.id))

        //then:
        actions.andExpect(status().isOk)
            .andExpect(view().name("medicine/detail"))
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("薬詳細が見つからなかった場合、NotFoundエラー画面を表示する")
    fun medicineDetailNotFound_displayNotFoundErrorPage() {
        //given:
        val badMedicineId = MedicineId("NonexistentId")

        //when:
        val actions = mockMvc.perform(get(PATH, badMedicineId))

        //then:
        actions.andExpect(status().isNotFound)
            .andExpect(view().name("error/notFoundError"))
    }

    @Test
    @DisplayName("未認証ユーザによるリクエストの場合、ホーム画面へリダイレクトする")
    fun requestedByUnauthenticatedUser_redirectsToHomePage() {
        //given:
        val medicineId = MedicineId("medicineId")

        //when:
        val actions = mockMvc.perform(get(PATH, medicineId))

        //then:
        actions.andExpect(status().isFound)
            .andExpect(redirectedUrl("/"))
    }
}