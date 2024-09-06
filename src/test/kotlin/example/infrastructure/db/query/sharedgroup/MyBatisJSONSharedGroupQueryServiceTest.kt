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
        val (_, member1OfJoinedSharedGroup) = testAccountInserter.insertAccountAndProfile()
        val (_, member2OfJoinedSharedGroup) = testAccountInserter.insertAccountAndProfile()
        val joinedSharedGroup =
                testSharedGroupInserter.insert(members = setOf(requester.accountId,
                                                               member1OfJoinedSharedGroup.accountId,
                                                               member2OfJoinedSharedGroup.accountId),
                                               pendingInvitations = emptySet())

        //when:
        val actual = jsonSharedGroupQueryService.findJSONJoinedSharedGroup(userSession)

        //then:
        assertThat(actual.sharedGroupId).isEqualTo(joinedSharedGroup.id.value)
        assertThat(actual.members)
            .extracting("accountId")
            .containsExactlyInAnyOrder(requester.accountId.value,
                                       member1OfJoinedSharedGroup.accountId.value,
                                       member2OfJoinedSharedGroup.accountId.value)
        val actualUser = actual.members.find { it.accountId == requester.accountId.value }
        assertThat(actualUser)
            .usingRecursiveComparison()
            .isEqualTo(JSONUser(requester.accountId.value,
                                requester.username.value,
                                requester.profileImageURL?.toURL()))
    }
}
