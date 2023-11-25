package example.presentation.controller.api.profile

import example.application.service.account.*
import example.presentation.shared.usersession.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import example.testhelper.springframework.security.*
import org.assertj.core.api.InstanceOfAssertFactories.*
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
internal class ProfileImageDeletionApiControllerTest(@Autowired private val mockMvc: MockMvc,
                                                     @Autowired private val accountService: AccountService,
                                                     @Autowired private val userSessionProvider: UserSessionProvider) {
    companion object {
        private const val PATH = "/api/profile/image/delete"
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("プロフィール画像の削除に成功した場合、ステータスコード204のレスポンスを返す")
    fun profileImageDeletionSucceeds_returnsResponseWithStatus204() {
        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf()))

        //then:
        actions.andExpect(status().isNoContent)
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("アカウントが削除済みの場合、ステータスコード204のレスポンスを返す")
    fun accountHasBeenDeleted_returnsResponseWithStatus204() {
        //then:
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        accountService.deleteAccount(userSession)

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf()))

        //then:
        actions.andExpect(status().isNoContent)
    }

    @Test
    @DisplayName("未認証ユーザからリクエストされた場合、ステータスコード401のレスポンスを返す")
    fun requestedByUnauthenticatedUser_returnsResponseWithStatus401() {
        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf()))

        //then:
        actions.andExpect(status().isUnauthorized)
    }
}