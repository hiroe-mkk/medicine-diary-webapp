package example.presentation.controller.api.sharedgroup

import example.domain.model.account.*
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
internal class SharedGroupRejectApiControllerTest(@Autowired private val mockMvc: MockMvc,
                                                  @Autowired private val testAccountInserter: TestAccountInserter,
                                                  @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                                  @Autowired private val userSessionProvider: UserSessionProvider) {
    companion object {
        private const val PATH = "/api/shared-group/reject"
    }

    private lateinit var user1AccountId: AccountId

    @BeforeEach
    internal fun setUp() {
        user1AccountId = testAccountInserter.insertAccountAndProfile().first.id
    }

    // TODO
    /*    @Test
        @WithMockAuthenticatedAccount
        @DisplayName("共有グループへの招待の拒否に成功した場合、ステータスコード204のレスポンスを返す")
        fun sharedGroupRejectSucceeds_returnsResponseWithStatus204() {
            //given:
            testSharedGroupInserter.insert(members = setOf(user1AccountId))
            val inviteCode = ""

            //when:
            val actions = mockMvc.perform(post(PATH)
                                              .with(csrf())
                                              .param("code", inviteCode))

            //then:
            actions.andExpect(status().isNoContent)
        }*/

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("共有グループが見つからなかった場合、ステータスコード204のレスポンスを返す")
    fun sharedGroupNotFound_returnsResponseWithStatus204() {
        //given:
        val inviteCode = ""

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("code", inviteCode))

        //then:
        actions.andExpect(status().isNoContent)
    }

    @Test
    @DisplayName("未認証ユーザによるリクエストの場合、ステータスコード401のレスポンスを返す")
    fun requestedByUnauthenticatedUser_returnsResponseWithStatus401() {
        //given:
        val inviteCode = ""

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("code", inviteCode))

        //then:
        actions.andExpect(status().isUnauthorized)
    }
}
