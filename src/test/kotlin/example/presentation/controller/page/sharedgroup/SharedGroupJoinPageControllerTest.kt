package example.presentation.controller.page.sharedgroup

import example.domain.model.sharedgroup.*
import example.domain.shared.type.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import example.testhelper.springframework.security.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ControllerTest
class SharedGroupJoinPageControllerTest(@Autowired private val mockMvc: MockMvc,
                                        @Autowired private val testAccountInserter: TestAccountInserter,
                                        @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                        @Autowired private val localDateTimeProvider: LocalDateTimeProvider) {
    companion object {
        private const val PATH = "/shared-group/join"
    }

    private lateinit var pendingInvitation: PendingInvitation

    @BeforeEach
    internal fun setUp() {
        pendingInvitation = SharedGroupFactory.createPendingInvitation(invitedOn = localDateTimeProvider.today())
        testSharedGroupInserter.insert(
                members = setOf(testAccountInserter.insertAccountAndProfile().first.id),
                pendingInvitations = setOf(pendingInvitation))
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("共有グループ参加画面を表示する")
    fun displaySharedGroupJoinPage() {
        //when:
        val actions = mockMvc.perform(get(PATH)
                                          .param("code", pendingInvitation.inviteCode))

        //then:
        actions.andExpect(status().isOk)
            .andExpect(view().name("sharedgroup/join"))
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("無効な招待だった場合、共有グループ参加画面を表示する")
    fun invalidInviteCode_displaySharedGroupJoinPage() {
        //given:
        val invalidInviteCode = ""

        //when:
        val actions = mockMvc.perform(get(PATH)
                                          .param("code", invalidInviteCode))

        //then:
        actions.andExpect(status().isOk)
            .andExpect(view().name("sharedgroup/join"))
    }

    @Test
    @DisplayName("未認証ユーザによるリクエストの場合、ホーム画面へリダイレクトする")
    fun requestedByUnauthenticatedUser_redirectToHomePage() {
        //when:
        val actions = mockMvc.perform(get(PATH)
                                          .param("code", pendingInvitation.inviteCode))

        //then:
        actions.andExpect(status().isFound)
            .andExpect(redirectedUrl("/"))
    }
}
