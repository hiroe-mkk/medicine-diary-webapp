package example.presentation.controller.api.sharedgroup

import example.domain.model.account.*
import example.domain.model.sharedgroup.*
import example.infrastructure.repository.shared.*
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
internal class ShareApiControllerTest(@Autowired private val mockMvc: MockMvc,
                                      @Autowired private val testAccountInserter: TestAccountInserter,
                                      @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                      @Autowired private val userSessionProvider: UserSessionProvider) {
    companion object {
        private const val PATH = "/api/shared-group/share"
    }

    private lateinit var user1AccountId: AccountId

    @BeforeEach
    internal fun setUp() {
        user1AccountId = testAccountInserter.insertAccountAndProfile().first.id
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("共有に成功した場合、ステータスコード204のレスポンスを返す")
    fun shareSucceeds_returnsResponseWithStatus204() {
        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("accountId", user1AccountId.toString()))

        //then:
        actions.andExpect(status().isNoContent)
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("共有に失敗した場合、ステータスコード209のレスポンスを返す")
    fun shareFails_returnsResponseWithStatus209() {
        //given:
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        testSharedGroupInserter.insert(members = setOf(userSession.accountId))

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("accountId", user1AccountId.toString()))

        //then:
        actions.andExpect(status().isConflict)
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("アカウントが見つからなかった場合、ステータスコード409のレスポンスを返す")
    fun accountNotFound_returnsResponseWithStatus404() {
        //given:
        val nonexistentAccountId = AccountId(EntityIdHelper.generate())

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("accountId", nonexistentAccountId.toString()))

        //then:
        actions.andExpect(status().isConflict)
    }

    @Test
    @DisplayName("未認証ユーザによるリクエストの場合、ステータスコード401のレスポンスを返す")
    fun requestedByUnauthenticatedUser_returnsResponseWithStatus401() {
        //given:
        val sharedGroupId = SharedGroupId("sharedGroupId")

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("sharedGroupId", sharedGroupId.toString())
                                          .param("accountId", user1AccountId.toString()))

        //then:
        actions.andExpect(status().isUnauthorized)
    }
}