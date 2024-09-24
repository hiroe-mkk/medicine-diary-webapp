package example.presentation.controller.api.profile

import example.application.service.account.*
import example.presentation.shared.usersession.*
import example.testhelper.springframework.autoconfigure.*
import example.testhelper.springframework.security.*
import net.bytebuddy.utility.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ControllerTest
class UsernameChangeApiControllerTest(@Autowired private val mockMvc: MockMvc,
                                      @Autowired private val accountDeletionService: AccountDeletionService,
                                      @Autowired private val userSessionProvider: UserSessionProvider) {
    companion object {
        private const val PATH = "/api/profile"
    }

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

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("アカウントが削除済みの場合、ステータスコード404のレスポンスを返す")
    fun accountHasBeenDeleted_returnsResponseWithStatus404() {
        //then:
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        accountDeletionService.deleteAccount(userSession)

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("username", newUsername))

        //then:
        actions.andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("未認証ユーザによるリクエストの場合、ステータスコード401のレスポンスを返す")
    fun requestedByUnauthenticatedUser_returnsResponseWithStatus401() {
        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("username", newUsername))

        //then:
        actions.andExpect(status().isUnauthorized)
    }
}
