package example.presentation.controller.api.sharedgroup

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
internal class SharedGroupLeaveApiControllerTest(@Autowired private val mockMvc: MockMvc,
                                                 @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                                 @Autowired private val userSessionProvider: UserSessionProvider) {
    companion object {
        private const val PATH = "/api/shared-group/leave"
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("共有グループからの脱退に成功した場合、ステータスコード204のレスポンスを返す")
    fun sharedGroupLeaveSucceeds_returnsResponseWithStatus204() {
        //given:
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        testSharedGroupInserter.insert(members = setOf(userSession.accountId))

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf()))

        //then:
        actions.andExpect(status().isNoContent)
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("共有グループが見つからなかった場合、ステータスコード204のレスポンスを返す")
    fun sharedGroupNotFound_returnsResponseWithStatus204() {
        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf()))

        //then:
        actions.andExpect(status().isNoContent)
    }

    @Test
    @DisplayName("未認証ユーザによるリクエストの場合、ステータスコード401のレスポンスを返す")
    fun requestedByUnauthenticatedUser_returnsResponseWithStatus401() {
        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf()))

        //then:
        actions.andExpect(status().isUnauthorized)
    }
}
