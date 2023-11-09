package example.infrastructure.query.user

import example.application.query.user.*
import example.domain.model.account.profile.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@MyBatisQueryServiceTest
internal class MyBatisUserQueryServiceTest(@Autowired private val userQueryService: UserQueryService,
                                           @Autowired private val testAccountInserter: TestAccountInserter) {
    @Test
    @DisplayName("ユーザーをキーワード検索する")
    fun searchForUsersByKeyword() {
        //given:
        val (userAccount, _) = testAccountInserter.insertAccountAndProfile(username = Username("user"))
        val userSession = UserSessionFactory.create(userAccount.id)
        val (anotherUser1Account, _) = testAccountInserter.insertAccountAndProfile(username = Username("user1"))
        val (anotherUser2Account, _) = testAccountInserter.insertAccountAndProfile(username = Username("user2"))

        //when:
        val actual = userQueryService.findByKeyword("user", userSession)

        //when:
        assertThat(actual)
            .extracting("accountId")
            .containsExactlyInAnyOrder(anotherUser1Account.id, anotherUser2Account.id)
    }
}