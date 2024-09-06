package example.infrastructure.db.query.user

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
internal class MyBatisJSONUserQueryServiceTest(@Autowired private val jsonUserQueryService: JSONUserQueryService,
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
    @DisplayName("ユーザーを取得する")
    fun getUser() {
        //when:
        val actual = jsonUserQueryService.findJSONUser(userSession.accountId)

        //then:
        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(JSONUser(requesterProfile.accountId.value,
                                requesterProfile.username.value,
                                requesterProfile.profileImageURL?.toURL()))
    }

    @Test
    @DisplayName("共有グループメンバー一覧を取得する")
    fun getSharedGroupMember() {
        //given:
        val (_, member1) = testAccountInserter.insertAccountAndProfile()
        val (_, member2) = testAccountInserter.insertAccountAndProfile()
        val sharedGroup = testSharedGroupInserter.insert(members = setOf(requesterProfile.accountId,
                                                                         member1.accountId,
                                                                         member2.accountId))

        //when:
        val actual = jsonUserQueryService.findJSONSharedGroupMember(sharedGroup.id, userSession)

        //then:
        assertThat(actual.users)
            .extracting("accountId")
            .containsExactlyInAnyOrder(member1.accountId.value, member2.accountId.value)
    }
}
