package example.presentation.controller.api.user

import example.infrastructure.db.repository.shared.*
import example.presentation.shared.usersession.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import example.testhelper.springframework.security.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ControllerTest
internal class UsersApiControllerTest(@Autowired private val mockMvc: MockMvc,
                                      @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                      @Autowired private val userSessionProvider: UserSessionProvider) {
    companion object {
        private const val PATH = "/api/users"
    }

    @Nested
    inner class GetSharedGroupMemberTest {
        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("共有グループメンバー一覧を取得する")
        fun getSharedGroupMember() {
            //given:
            val userSession = userSessionProvider.getUserSessionOrElseThrow()
            val sharedGroup = testSharedGroupInserter.insert(members = setOf(userSession.accountId))

            //when:
            val actions = mockMvc.perform(get(PATH)
                                              .queryParam("sharedGroupId", sharedGroup.id.toString()))

            //then:
            actions.andExpect(status().isOk)
                .andExpect(header().string("Content-Type", "application/json"))
        }

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("無効な形式の共有グループIDの場合、ステータスコード400のレスポンスを返す")
        fun invalidSharedGroupId_returnsResponseWithStatus400() {
            //given:
            val invalidSharedGroupId = "invalidSharedGroupId"

            //when:
            val actions = mockMvc.perform(get(PATH)
                                              .queryParam("sharedGroupId", invalidSharedGroupId))

            //then:
            actions.andExpect(status().isBadRequest)
        }


        @Test
        @DisplayName("未認証ユーザによるリクエストの場合、ステータスコード401のレスポンスを返す")
        fun requestedByUnauthenticatedUser_returnsResponseWithStatus401() {
            //given:
            val sharedGroupId = EntityIdHelper.generate()

            //when:
            val actions = mockMvc.perform(get(PATH)
                                              .queryParam("sharedGroupId", sharedGroupId))

            //then:
            actions.andExpect(status().isUnauthorized)
        }
    }
}
