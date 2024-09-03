package example.presentation.controller.api.medicine

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
internal class MedicineImageDeletionApiControllerTest(@Autowired private val mockMvc: MockMvc,
                                                      @Autowired private val testMedicineInserter: TestMedicineInserter,
                                                      @Autowired private val userSessionProvider: UserSessionProvider) {
    companion object {
        private const val PATH = "/api/medicines/{medicineId}/image/delete"
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("薬画像の削除に成功した場合、ステータスコード204のレスポンスを返す")
    fun medicineImageDeletionSucceeds_returnsResponseWithStatus204() {
        //given:
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))

        //when:
        val actions = mockMvc.perform(post(PATH, medicine.id)
                                          .with(csrf()))

        //then:
        actions.andExpect(status().isNoContent)
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("薬が見つからなかった場合、ステータスコード204のレスポンスを返す")
    fun medicineNotFound_returnsResponseWithStatus204() {
        //given:
        val nonexistentMedicineId = MedicineId(EntityIdHelper.generate())

        //when:
        val actions = mockMvc.perform(post(PATH, nonexistentMedicineId)
                                          .with(csrf()))

        //then:
        actions.andExpect(status().isNoContent)
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("無効な形式の薬IDの場合、ステータスコード400のレスポンスを返す")
    fun invalidMedicineId_returnsResponseWithStatus400() {
        //given:
        val invalidMedicineId = MedicineId("invalidMedicineId")

        //when:
        val actions = mockMvc.perform(post(PATH, invalidMedicineId)
                                          .with(csrf()))

        //then:
        actions.andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("未認証ユーザからリクエストされた場合、ステータスコード401のレスポンスを返す")
    fun requestedByUnauthenticatedUser_returnsResponseWithStatus401() {
        //given:
        val medicineId = MedicineId(EntityIdHelper.generate())

        //when:
        val actions = mockMvc.perform(post(PATH, medicineId)
                                          .with(csrf()))

        //then:
        actions.andExpect(status().isUnauthorized)
    }
}
