package example.presentation.controller.api.medicine

import example.application.service.medicine.*
import example.domain.model.medicine.*
import example.presentation.controller.page.medicine.*
import example.testhelper.factory.*
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
internal class MedicineRegistrationApiControllerTest(@Autowired private val mockMvc: MockMvc) {
    companion object {
        private const val PATH = "/api/medicines/register"
    }

    private val name = "ロキソニンS"
    private val takingUnit = "錠"
    private val quantity = 1.0
    private val timesPerDay = 3
    private val timingOptions = arrayOf(Timing.AS_NEEDED)
    private val effects = arrayOf("頭痛", "解熱", "肩こり")
    private val precautions = "服用間隔は4時間以上開けること。"

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("薬の登録に成功した場合、ステータスコード201のレスポンスを返す")
    fun medicineRegistrationSucceeds_returnsResponseWithStatus200() {
        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("name", name)
                                          .param("quantity", quantity.toString())
                                          .param("takingUnit", takingUnit)
                                          .param("timesPerDay", timesPerDay.toString())
                                          .param("timingOptions", timingOptions[0].name)
                                          .param("effects", effects[0])
                                          .param("effects", effects[1])
                                          .param("effects", effects[2])
                                          .param("precautions", precautions))

        //then:
        actions.andExpect(status().isCreated)
            .andExpect(header().string("Content-Type", "application/json"))
            .andExpect(jsonPath("\$.medicineId").isNotEmpty)
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("バリデーションエラーが発生した場合、ステータスコード400のレスポンスを返す")
    fun validationErrorOccurs_returnsResponseWithStatus400() {
        //given:
        val invalidName = ""

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("name", invalidName)
                                          .param("quantity", quantity.toString())
                                          .param("takingUnit", takingUnit)
                                          .param("timesPerDay", timesPerDay.toString())
                                          .param("timingOptions", timingOptions[0].name)
                                          .param("effects", effects[0])
                                          .param("effects", effects[1])
                                          .param("effects", effects[2])
                                          .param("precautions", precautions))

        //then:
        actions.andExpect(status().isBadRequest)
            .andExpect(header().string("Content-Type", "application/json"))
            .andExpect(jsonPath("\$.fieldErrors.name").isNotEmpty)
    }

    @Test
    @DisplayName("未認証ユーザによるリクエストの場合、ステータスコード401のレスポンスを返す")
    fun requestedByUnauthenticatedUser_returnsResponseWithStatus401() {
        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("name", name)
                                          .param("quantity", quantity.toString())
                                          .param("takingUnit", takingUnit)
                                          .param("timesPerDay", timesPerDay.toString())
                                          .param("timingOptions", timingOptions[0].name)
                                          .param("effects", effects[0])
                                          .param("effects", effects[1])
                                          .param("effects", effects[2])
                                          .param("precautions", precautions))

        //then:
        actions.andExpect(status().isUnauthorized)
    }
}