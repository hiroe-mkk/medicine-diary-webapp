package example.presentation.controller.api.user

import example.domain.model.medicine.*
import example.presentation.shared.usersession.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import example.testhelper.springframework.security.*
import org.assertj.core.api.InstanceOfAssertFactories.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ControllerTest
internal class UsersApiControllerTest(@Autowired private val mockMvc: MockMvc) {
    companion object {
        private const val PATH = "/api/users"
    }

    @Nested
    inner class GetUserTest {
        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("ユーザーを取得する")
        fun getUser() {
            //when:
            val actions = mockMvc.perform(get("${PATH}?self"))

            //then:
            actions.andExpect(status().isOk)
                .andExpect(header().string("Content-Type", "application/json"))
        }

        @Test
        @DisplayName("未認証ユーザによるリクエストの場合、ステータスコード401のレスポンスを返す")
        fun requestedByUnauthenticatedUser_returnsResponseWithStatus401() {
            //when:
            val actions = mockMvc.perform(get("${PATH}?own"))

            //then:
            actions.andExpect(status().isUnauthorized)
        }
    }

    @Nested
    inner class GetMemberUsersTest {
        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("メンバーユーザー一覧を取得する")
        fun getMemberUsers() {
            //when:
            val actions = mockMvc.perform(get("${PATH}?members"))

            //then:
            actions.andExpect(status().isOk)
                .andExpect(header().string("Content-Type", "application/json"))
        }

        @Test
        @DisplayName("未認証ユーザによるリクエストの場合、ステータスコード401のレスポンスを返す")
        fun requestedByUnauthenticatedUser_returnsResponseWithStatus401() {
            //when:
            val actions = mockMvc.perform(get("${PATH}?member"))

            //then:
            actions.andExpect(status().isUnauthorized)
        }
    }

    @Nested
    inner class GetUsersByKeywordTest {
        private val keyword = "username"

        @Test
        @WithMockAuthenticatedAccount
        @DisplayName("キーワードでユーザー一覧を取得する")
        fun getUsersByKeyword() {
            //when:
            val actions = mockMvc.perform(get("${PATH}?keyword=${keyword}"))

            //then:
            actions.andExpect(status().isOk)
                .andExpect(header().string("Content-Type", "application/json"))
        }

        @Test
        @DisplayName("未認証ユーザによるリクエストの場合、ステータスコード401のレスポンスを返す")
        fun requestedByUnauthenticatedUser_returnsResponseWithStatus401() {
            //when:
            val actions = mockMvc.perform(get("${PATH}?keyword=${keyword}"))

            //then:
            actions.andExpect(status().isUnauthorized)
        }
    }
}