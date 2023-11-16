package example.presentation.controller.api.profile

import example.application.service.account.*
import example.presentation.shared.usersession.*
import example.testhelper.factory.*
import example.testhelper.springframework.autoconfigure.*
import example.testhelper.springframework.security.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.http.*
import org.springframework.mock.web.*
import org.springframework.security.test.web.servlet.request.*
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ControllerTest
internal class ProfileImageChangeApiControllerTest(@Autowired private val mockMvc: MockMvc,
                                                   @Autowired private val accountService: AccountService,
                                                   @Autowired private val userSessionProvider: UserSessionProvider) {
    companion object {
        private const val PATH = "/api/profile/image/change"
    }

    private val multipartFile = TestImageFactory.createMultipartFile() as MockMultipartFile

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("プロフィール画像の変更に成功した場合、ステータスコード204のレスポンスを返す")
    fun profileImageChangeSucceeds_returnsResponseWithStatus204() {
        val actions = mockMvc.perform(multipart(PATH)
                                          .file(multipartFile)
                                          .with(SecurityMockMvcRequestPostProcessors.csrf()))

        //then:
        actions.andExpect(status().isNoContent)
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("バリデーションエラーが発生した場合、ステータスコード400のレスポンスを返す")
    fun validationErrorOccurs_returnsResponseWithStatus400() {
        //given:
        val invalidMultipartFile =
                TestImageFactory.createMultipartFile(type = MediaType.IMAGE_PNG) as MockMultipartFile

        //when:
        val actions = mockMvc.perform(multipart(PATH)
                                          .file(invalidMultipartFile)
                                          .with(SecurityMockMvcRequestPostProcessors.csrf()))

        //then:
        actions.andExpect(status().isBadRequest)
            .andExpect(header().string("Content-Type", "application/json"))
            .andExpect(jsonPath("\$.fieldErrors.image").isNotEmpty)
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("アカウントが削除済みの場合、ステータスコード404のレスポンスを返す")
    fun accountHasBeenDeleted_returnsResponseWithStatus404() {
        //then:
        val userSession = userSessionProvider.getUserSession()
        accountService.deleteAccount(userSession)

        //when:
        val actions = mockMvc.perform(multipart(PATH)
                                          .file(multipartFile)
                                          .with(SecurityMockMvcRequestPostProcessors.csrf()))

        //then:
        actions.andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("未認証ユーザからリクエストされた場合、ステータスコード401のレスポンスを返す")
    fun requestedByUnauthenticatedUser_returnsResponseWithStatus401() {
        //when:
        val actions = mockMvc.perform(multipart(PATH)
                                          .file(multipartFile)
                                          .with(SecurityMockMvcRequestPostProcessors.csrf()))

        //then:
        actions.andExpect(status().isUnauthorized)
    }
}