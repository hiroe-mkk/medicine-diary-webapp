package example.presentation.controller.api.medicine

import example.testhelper.springframework.autoconfigure.*
import example.testhelper.springframework.security.*
import net.bytebuddy.utility.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ControllerTest
internal class MedicineOverviewsApiControllerTest(@Autowired private val mockMvc: MockMvc) {
    companion object {
        private const val PATH = "/api/medicines"
    }

    @Nested
    inner class GetMedicineOverviewsTest {
        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("薬概要一覧を取得する")
        fun getMedicineOverviews() {
            //when:
            val actions = mockMvc.perform(get(PATH)
                                              .param("effect", "頭痛"))

            //then:
            actions.andExpect(status().isOk)
                .andExpect(header().string("Content-Type", "application/json"))
        }

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("バリデーションエラーが発生した場合、ステータスコード400のレスポンスを返す")
        fun validationErrorOccurs_returnsResponseWithStatus400() {
            //when:
            val actions = mockMvc.perform(get(PATH)
                                              .param("effect", RandomString(31).nextString()))

            //then:
            actions.andExpect(status().isBadRequest)
        }

        @Test
        @DisplayName("未認証ユーザによるリクエストの場合、ステータスコード401のレスポンスを返す")
        fun requestedByUnauthenticatedUser_returnsResponseWithStatus401() {
            //when:
            val actions = mockMvc.perform(get(PATH))

            //then:
            actions.andExpect(status().isUnauthorized)
        }
    }

    @Nested
    inner class GetAvailableMedicineOverviewsTest {
        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("服用可能な薬概要一覧を取得する")
        fun getAvailableMedicineOverviews() {
            //when:
            val actions = mockMvc.perform(get("${PATH}?available"))

            //then:
            actions.andExpect(status().isOk)
                .andExpect(header().string("Content-Type", "application/json"))
        }

        @Test
        @DisplayName("未認証ユーザによるリクエストの場合、ステータスコード401のレスポンスを返す")
        fun requestedByUnauthenticatedUser_returnsResponseWithStatus401() {
            //when:
            val actions = mockMvc.perform(get("${PATH}?available"))

            //then:
            actions.andExpect(status().isUnauthorized)
        }
    }
}