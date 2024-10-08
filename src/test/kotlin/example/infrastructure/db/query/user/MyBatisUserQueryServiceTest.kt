package example.infrastructure.db.query.user

import example.application.query.user.*
import example.application.service.account.*
import example.application.shared.usersession.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@MyBatisQueryServiceTest
internal class MyBatisUserQueryServiceTest(@Autowired private val userQueryService: UserQueryService,
                                           @Autowired private val testAccountInserter: TestAccountInserter,
                                           @Autowired private val testSharedGroupInserter: TestSharedGroupInserter) {
    private lateinit var userSession: UserSession

    @BeforeEach
    internal fun setUp() {
        val requester = testAccountInserter.insertAccountAndProfile().second
        userSession = UserSessionFactory.create(requester.accountId)
    }


    @Test
    @DisplayName("共有グループのメンバーの場合、ユーザーの取得に成功する")
    fun targetIsSharedGroupMember_gettingUserSucceeds() {
        //given:
        val member = testAccountInserter.insertAccountAndProfile().second
        testSharedGroupInserter.insert(members = setOf(userSession.accountId,
                                                       member.accountId))
        //when:
        val actual = userQueryService.getUser(member.accountId, userSession)

        //then:
        assertThat(actual).isEqualTo(User(member.accountId,
                                          member.username,
                                          member.profileImageURL))
    }

    @Test
    @DisplayName("共有グループのメンバーではない場合、ユーザーの取得に失敗する")
    fun targetIsNotSharedGroupMember_gettingUserFails() {
        //given:
        val user = testAccountInserter.insertAccountAndProfile().second
        testSharedGroupInserter.insert(members = setOf(userSession.accountId))

        //when:
        val target: () -> Unit = {
            userQueryService.getUser(user.accountId, userSession)
        }

        //then:
        val accountNotFoundException = assertThrows<AccountNotFoundException>(target)
        assertThat(accountNotFoundException.accountId).isEqualTo(user.accountId)
    }
}
