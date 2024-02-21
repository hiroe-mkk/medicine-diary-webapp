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
internal class ParticipationInSharedGroupApiControllerTest(@Autowired private val mockMvc: MockMvc,
                                                           @Autowired private val testAccountInserter: TestAccountInserter,
                                                           @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                                           @Autowired private val userSessionProvider: UserSessionProvider) {
    companion object {
        private const val PATH = "/api/shared-group/participate"
    }

    private lateinit var user1AccountId: AccountId

    @BeforeEach
    internal fun setUp() {
        user1AccountId = testAccountInserter.insertAccountAndProfile().first.id
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("共有グループへの参加に成功した場合、ステータスコード204のレスポンスを返す")
    fun participateInSharedGroupSucceeds_returnsResponseWithStatus204() {
        //given:
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        val sharedGroup = testSharedGroupInserter.insert(members = setOf(user1AccountId),
                                                         invitees = setOf(userSession.accountId))

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("sharedGroupId", sharedGroup.id.toString()))

        //then:
        actions.andExpect(status().isNoContent)
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("共有グループへの参加に失敗した場合、ステータスコード209のレスポンスを返す")
    fun participationInSharedGroupFails_returnsResponseWithStatus209() {
        //given:
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        val sharedGroup = testSharedGroupInserter.insert(members = setOf(user1AccountId, userSession.accountId),
                                                         invitees = setOf(userSession.accountId))

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("sharedGroupId", sharedGroup.id.toString()))

        //then:
        actions.andExpect(status().isConflict)
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("共有グループが見つからなかった場合、ステータスコード409のレスポンスを返す")
    fun sharedGroupNotFound_returnsResponseWithStatus404() {
        //given:
        val nonexistentSharedGroupId = SharedGroupId(EntityIdHelper.generate())

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("sharedGroupId", nonexistentSharedGroupId.toString()))

        //then:
        actions.andExpect(status().isConflict)
    }

    @Test
    @DisplayName("未認証ユーザによるリクエストの場合、ステータスコード401のレスポンスを返す")
    fun requestedByUnauthenticatedUser_returnsResponseWithStatus401() {
        //given:
        val sharedGroupId = SharedGroupId(EntityIdHelper.generate())

        //when:
        val actions = mockMvc.perform(post(PATH)
                                          .with(csrf())
                                          .param("sharedGroupId", sharedGroupId.toString()))

        //then:
        actions.andExpect(status().isUnauthorized)
    }
}