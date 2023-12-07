package example.presentation.controller.api.sharedgroup

import example.domain.model.account.*
import example.domain.model.sharedgroup.*
import example.presentation.shared.usersession.*
import example.testhelper.inserter.*
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
internal class InvitationToSharedGroupApiControllerTest(@Autowired private val mockMvc: MockMvc,
                                                        @Autowired private val testAccountInserter: TestAccountInserter,
                                                        @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                                        @Autowired private val userSessionProvider: UserSessionProvider) {
    companion object {
        private const val PATH = "/api/shared-group/invite"
    }

    private lateinit var user1AccountId: AccountId

    @BeforeEach
    internal fun setUp() {
        user1AccountId = testAccountInserter.insertAccountAndProfile().first.id
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("共有グループへの招待に成功した場合、ステータスコード204のレスポンスを返す")
    fun invitationToSharedGroupSucceeds_returnsResponseWithStatus204() {
        //given:
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        val sharedGroup = testSharedGroupInserter.insert(members = setOf(userSession.accountId))

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("sharedGroupId", sharedGroup.id.value)
                                          .param("accountId", user1AccountId.value))

        //then:
        actions.andExpect(status().isNoContent)
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("共有グループへの招待に失敗した場合、ステータスコード209のレスポンスを返す")
    fun invitationToSharedGroupFails_returnsResponseWithStatus209() {
        //given:
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        val sharedGroup = testSharedGroupInserter.insert(members = setOf(userSession.accountId, user1AccountId))

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("sharedGroupId", sharedGroup.id.value)
                                          .param("accountId", user1AccountId.value))

        //then:
        actions.andExpect(status().isConflict)
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("共有グループが見つからなかった場合、ステータスコード404のレスポンスを返す")
    fun sharedGroupNotFound_returnsResponseWithStatus404() {
        //given:
        val badSharedGroupId = SharedGroupId("NonexistentId")

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("sharedGroupId", badSharedGroupId.value)
                                          .param("accountId", user1AccountId.value))

        //then:
        actions.andExpect(status().isNotFound)
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("アカウントが見つからなかった場合、ステータスコード404のレスポンスを返す")
    fun accountNotFound_returnsResponseWithStatus404() {
        //given:
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        val sharedGroup = testSharedGroupInserter.insert(members = setOf(userSession.accountId, user1AccountId))
        val badAccountId = AccountId("NonexistentId")

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("sharedGroupId", sharedGroup.id.value)
                                          .param("accountId", badAccountId.value))

        //then:
        actions.andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("未認証ユーザによるリクエストの場合、ステータスコード401のレスポンスを返す")
    fun requestedByUnauthenticatedUser_returnsResponseWithStatus401() {
        //given:
        val sharedGroupId = SharedGroupId("sharedGroupId")

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("sharedGroupId", sharedGroupId.value)
                                          .param("accountId", user1AccountId.value))

        //then:
        actions.andExpect(status().isUnauthorized)
    }
}