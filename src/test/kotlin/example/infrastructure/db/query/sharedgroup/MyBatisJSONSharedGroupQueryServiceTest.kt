package example.infrastructure.db.query.sharedgroup

import example.application.query.sharedgroup.*
import example.application.query.user.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@MyBatisQueryServiceTest
internal class MyBatisJSONSharedGroupQueryServiceTest(@Autowired private val jsonSharedGroupQueryService: JSONSharedGroupQueryService,
                                                      @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                                      @Autowired private val testAccountInserter: TestAccountInserter) {
    @Test
    @DisplayName("共有グループを取得する")
    fun getSharedGroups() {
        //given:
        val requester = testAccountInserter.insertAccountAndProfile().second
        val userSession = UserSessionFactory.create(requester.accountId)

        // 参加している共有グループ
        val (_, member1OfJoinedSharedGroup) = testAccountInserter.insertAccountAndProfile()
        val (_, member2OfJoinedSharedGroup) = testAccountInserter.insertAccountAndProfile()
        val joinedSharedGroup =
                testSharedGroupInserter.insert(members = setOf(requester.accountId,
                                                               member1OfJoinedSharedGroup.accountId,
                                                               member2OfJoinedSharedGroup.accountId),
                                               invitees = emptySet())

        // 招待された共有グループ
        val (_, member1OfInvitedSharedGroup) = testAccountInserter.insertAccountAndProfile()
        val invitedSharedGroup = testSharedGroupInserter.insert(members = setOf(member1OfInvitedSharedGroup.accountId),
                                                                invitees = setOf(userSession.accountId))

        //when:
        val actual = jsonSharedGroupQueryService.findJSONSharedGroup(userSession)

        //then:
        assertThat(actual.joinedSharedGroup?.sharedGroupId).isEqualTo(joinedSharedGroup.id.value)
        assertThat(actual.joinedSharedGroup?.members)
            .extracting("accountId")
            .containsExactlyInAnyOrder(requester.accountId.value,
                                       member1OfJoinedSharedGroup.accountId.value,
                                       member2OfJoinedSharedGroup.accountId.value)
        assertThat(actual.joinedSharedGroup?.invitees).isEmpty()


        assertThat(actual.invitedSharedGroups)
            .extracting("sharedGroupId")
            .containsExactly(invitedSharedGroup.id.value)
        assertThat(actual.invitedSharedGroups.first().members)
            .extracting("accountId")
            .containsExactlyInAnyOrder(member1OfInvitedSharedGroup.accountId.value)
        assertThat(actual.invitedSharedGroups.first().invitees)
            .extracting("accountId")
            .containsExactly(requester.accountId.value)

        val actualUser = actual.joinedSharedGroup!!.members.find { it.accountId == requester.accountId.value }
        assertThat(actualUser)
            .usingRecursiveComparison()
            .isEqualTo(JSONUser(requester.accountId.value,
                                requester.username.value,
                                requester.profileImageURL?.toURL()))
    }
}
