package example.presentation.controller.api.profile

import example.domain.model.account.*
import example.presentation.controller.page.profile.*
import example.testhelper.factory.*
import example.testhelper.springframework.*
import example.testhelper.springframework.autoconfigure.*
import example.testhelper.springframework.security.*
import net.bytebuddy.utility.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.http.*
import org.springframework.mock.web.*
import org.springframework.security.test.web.servlet.request.*
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.web.multipart.*
import java.io.*

@ControllerTest
internal class ProfileEditApiControllerTest(@Autowired private val mockMvc: MockMvc) {
    companion object {
        private const val PATH = "/api/profile"
    }

    @Nested
    inner class ChangeUsernameTest {
        private val newUsername = "newUsername"

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("ユーザー名変更に成功した場合、ステータスコード204のレスポンスを返す")
        fun usernameChangeSucceeds_returnsResponseWithStatus204() {
            //when:
            val actions = mockMvc.perform(post("${PATH}/username/change")
                                              .with(csrf())
                                              .param("username", newUsername))

            //then:
            actions.andExpect(status().isNoContent)
        }

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("バリデーションエラーが発生した場合、ステータスコード400のレスポンスを返す")
        fun validationErrorOccurs_returnsResponseWithStatus400() {
            //given:
            val invalidUsername = RandomString(31).nextString()

            //when:
            val actions = mockMvc.perform(post("${PATH}/username/change")
                                              .with(csrf())
                                              .param("username", invalidUsername))

            //then:
            actions.andExpect(status().isBadRequest)
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("\$.fieldErrors.username").isNotEmpty)
        }
    }
}