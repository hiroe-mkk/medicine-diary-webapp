package example.infrastructure.query.user

import example.application.query.shared.type.*
import example.application.query.user.*
import example.application.shared.usersession.*
import example.domain.model.account.profile.*
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
    private lateinit var requesterProfile: Profile
    private lateinit var userSession: UserSession

    @BeforeEach
    internal fun setUp() {
        requesterProfile = testAccountInserter.insertAccountAndProfile().second
        userSession = UserSessionFactory.create(requesterProfile.accountId)
    }

    @Test
    @DisplayName("キーワードでユーザー一覧を取得する")
    fun getUsersByKeyword() {
        //given:
        val user1AccountId = testAccountInserter.insertAccountAndProfile(username = Username("user1")).first.id
        val user2AccountId = testAccountInserter.insertAccountAndProfile(username = Username("user2")).first.id

        //when:
        val actual = userQueryService.findJSONUsersByKeyword("user", userSession)

        //when:
        assertThat(actual.users)
            .extracting("accountId")
            .containsExactlyInAnyOrder(user1AccountId.value, user2AccountId.value)
    }

    @Test
    @DisplayName("メンバーユーザー一覧を取得する")
    fun getMemberUsers() {
        val (_, member1) = testAccountInserter.insertAccountAndProfile()
        val (_, member2) = testAccountInserter.insertAccountAndProfile()
        val (_, invitee1) = testAccountInserter.insertAccountAndProfile()
        testSharedGroupInserter.insert(members = setOf(requesterProfile.accountId,
                                                       member1.accountId,
                                                       member2.accountId),
                                       invitees = setOf(invitee1.accountId))

        //when:
        val actual = userQueryService.findMemberJSONUsers(userSession)

        //then:
        assertThat(actual.users)
            .extracting("accountId")
            .containsExactlyInAnyOrder(requesterProfile.accountId.value,
                                       member1.accountId.value,
                                       member2.accountId.value)
        val actualUser = actual.users.find { it.accountId == requesterProfile.accountId.value }
        assertThat(actualUser).isEqualTo(JSONUser(requesterProfile.accountId.value,
                                                  requesterProfile.username.value,
                                                  requesterProfile.profileImageURL?.toURL()))
    }
}