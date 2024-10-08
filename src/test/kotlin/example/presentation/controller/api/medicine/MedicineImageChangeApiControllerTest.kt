package example.presentation.controller.api.medicine

import example.domain.model.medicine.*
import example.infrastructure.db.repository.shared.*
import example.presentation.shared.usersession.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import example.testhelper.springframework.security.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.http.*
import org.springframework.mock.web.*
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ControllerTest
internal class MedicineImageChangeApiControllerTest(@Autowired private val mockMvc: MockMvc,
                                                    @Autowired private val testMedicineInserter: TestMedicineInserter,
                                                    @Autowired private val userSessionProvider: UserSessionProvider) {
    companion object {
        private const val PATH = "/api/medicines/{medicineId}/image/change"
    }

    private val multipartFile = TestImageFactory.createMultipartFile() as MockMultipartFile

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("薬画像の変更に成功した場合、ステータスコード204のレスポンスを返す")
    fun medicineImageChangeSucceeds_returnsResponseWithStatus204() {
        //given:
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))

        //when:
        val actions = mockMvc.perform(multipart(PATH, medicine.id)
                                          .file(multipartFile)
                                          .with(csrf()))

        //then:
        actions.andExpect(status().isNoContent)
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("バリデーションエラーが発生した場合、ステータスコード400のレスポンスを返す")
    fun validationErrorOccurs_returnsResponseWithStatus400() {
        //given:
        val medicineId = EntityIdHelper.generate()
        val invalidMultipartFile = TestImageFactory.createMultipartFile(type = MediaType.IMAGE_PNG) as MockMultipartFile

        //when:
        val actions = mockMvc.perform(multipart(PATH, medicineId)
                                          .file(invalidMultipartFile)
                                          .with(csrf()))

        //then:
        actions.andExpect(status().isBadRequest)
            .andExpect(header().string("Content-Type", "application/json"))
            .andExpect(jsonPath("\$.fieldErrors.image").isNotEmpty)
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("薬が見つからなかった場合、ステータスコード404のレスポンスを返す")
    fun medicineNotFound_returnsResponseWithStatus404() {
        //then:
        val nonexistentMedicineId = EntityIdHelper.generate()

        //when:
        val actions = mockMvc.perform(multipart(PATH, nonexistentMedicineId)
                                          .file(multipartFile)
                                          .with(csrf()))

        //then:
        actions.andExpect(status().isNotFound)
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("無効な形式の薬IDの場合、ステータスコード400のレスポンスを返す")
    fun invalidMedicineId_returnsResponseWithStatus400() {
        //given:
        val invalidMedicineId = "invalidMedicineId"

        //when:
        val actions = mockMvc.perform(multipart(PATH, invalidMedicineId)
                                          .file(multipartFile)
                                          .with(csrf()))

        //then:
        actions.andExpect(status().isBadRequest)
    }


    @Test
    @DisplayName("未認証ユーザからリクエストされた場合、ステータスコード401のレスポンスを返す")
    fun requestedByUnauthenticatedUser_returnsResponseWithStatus401() {
        //given:
        val medicineId = EntityIdHelper.generate()

        //when:
        val actions = mockMvc.perform(multipart(PATH, medicineId)
                                          .file(multipartFile)
                                          .with(csrf()))

        //then:
        actions.andExpect(status().isUnauthorized)
    }
}
