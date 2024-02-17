package example.presentation.controller.api.medicine

import example.domain.model.medicine.*
import example.presentation.shared.usersession.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import example.testhelper.springframework.security.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.security.test.web.servlet.request.*
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ControllerTest
internal class InventoryManagementStoppageApiControllerTest(@Autowired private val mockMvc: MockMvc,
                                                            @Autowired private val testMedicineInserter: TestMedicineInserter,
                                                            @Autowired private val userSessionProvider: UserSessionProvider) {
    companion object {
        private const val PATH = "/api/medicines/{medicineId}/inventory/stop"
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("在庫管理の終了に成功した場合、ステータスコード204のレスポンスを返す")
    fun inventoryManagementStoppageSucceeds_returnsResponseWithStatus204() {
        //given:
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))

        //when:
        val actions = mockMvc.perform(post(PATH, medicine.id)
                                          .with(SecurityMockMvcRequestPostProcessors.csrf()))

        //then:
        actions.andExpect(status().isNoContent)
    }

    @Test
    @DisplayName("未認証ユーザからリクエストされた場合、ステータスコード401のレスポンスを返す")
    fun requestedByUnauthenticatedUser_returnsResponseWithStatus401() {
        //given:
        val medicineId = MedicineId("medicineId")

        //when:
        val actions = mockMvc.perform(post(PATH, medicineId)
                                          .with(SecurityMockMvcRequestPostProcessors.csrf()))

        //then:
        actions.andExpect(status().isUnauthorized)
    }
}