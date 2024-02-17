package example.presentation.controller.api.medicine

import example.domain.model.medicine.*
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
internal class InventoryAdjustmentApiControllerTest(@Autowired private val mockMvc: MockMvc,
                                                    @Autowired private val testMedicineInserter: TestMedicineInserter,
                                                    @Autowired private val userSessionProvider: UserSessionProvider) {
    companion object {
        private const val PATH = "/api/medicines/{medicineId}/inventory/adjust"
    }

    val remainingQuantity: String = "1.0"
    val quantityPerPackage: String = "12.0"
    val startedOn: String = "2020-01-01"
    val unusedPackage: String = "2"

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("在庫の修正に成功した場合、ステータスコード204のレスポンスを返す")
    fun inventoryAdjustmentSucceeds_returnsResponseWithStatus204() {
        //given:
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))

        //when:
        val actions = mockMvc.perform(post(PATH, medicine.id)
                                          .with(csrf())
                                          .param("remainingQuantity", remainingQuantity)
                                          .param("quantityPerPackage", quantityPerPackage)
                                          .param("startedOn", startedOn)
                                          .param("unusedPackage", unusedPackage))
        //then:
        actions.andExpect(status().isNoContent)
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("バリデーションエラーが発生した場合、ステータスコード400のレスポンスを返す")
    fun validationErrorOccurs_returnsResponseWithStatus400() {
        //given:
        val medicineId = MedicineId("medicineId")
        val invalidRemainingQuantity = "-1.0"

        //when:
        val actions = mockMvc.perform(post(PATH, medicineId)
                                          .with(csrf())
                                          .param("remainingQuantity", invalidRemainingQuantity)
                                          .param("quantityPerPackage", quantityPerPackage)
                                          .param("startedOn", startedOn)
                                          .param("unusedPackage", unusedPackage))

        //then:
        actions.andExpect(status().isBadRequest)
            .andExpect(header().string("Content-Type", "application/json"))
            .andExpect(jsonPath("\$.fieldErrors.remainingQuantity").isNotEmpty)
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("薬が見つからなかった場合、ステータスコード404のレスポンスを返す")
    fun medicineNotFound_returnsResponseWithStatus404() {
        //then:
        val badMedicineId = MedicineId("NonexistentId")

        //when:
        val actions = mockMvc.perform(post(PATH, badMedicineId)
                                          .with(csrf())
                                          .param("remainingQuantity", remainingQuantity)
                                          .param("quantityPerPackage", quantityPerPackage)
                                          .param("startedOn", startedOn)
                                          .param("unusedPackage", unusedPackage))

        //then:
        actions.andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("未認証ユーザからリクエストされた場合、ステータスコード401のレスポンスを返す")
    fun requestedByUnauthenticatedUser_returnsResponseWithStatus401() {
        //given:
        val medicineId = MedicineId("medicineId")

        //when:
        val actions = mockMvc.perform(post(PATH, medicineId)
                                          .with(csrf()))

        //then:
        actions.andExpect(status().isUnauthorized)
    }
}