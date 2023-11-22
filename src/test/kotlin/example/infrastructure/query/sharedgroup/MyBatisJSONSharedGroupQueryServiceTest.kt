package example.infrastructure.query.sharedgroup

import example.application.query.sharedgroup.*
import example.application.query.user.*
import example.application.shared.usersession.*
import example.domain.model.account.profile.*
import example.domain.model.account.profile.profileimage.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import okhttp3.internal.*
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
        val (_, member1OfParticipatingSharedGroup) = testAccountInserter.insertAccountAndProfile()
        val (_, member2OfParticipatingSharedGroup) = testAccountInserter.insertAccountAndProfile()
        val participatingSharedGroup =
                testSharedGroupInserter.insert(members = setOf(requester.accountId,
                                                               member1OfParticipatingSharedGroup.accountId,
                                                               member2OfParticipatingSharedGroup.accountId),
                                               invitees = emptySet())

        // 招待された共有グループ
        val (_, member1OfInvitedSharedGroup) = testAccountInserter.insertAccountAndProfile()
        val invitedSharedGroup = testSharedGroupInserter.insert(members = setOf(member1OfInvitedSharedGroup.accountId),
                                                                invitees = setOf(userSession.accountId))

        //when:
        val actual = jsonSharedGroupQueryService.findJSONSharedGroup(userSession)

        //then:
        assertThat(actual.participatingSharedGroup?.sharedGroupId).isEqualTo(participatingSharedGroup.id.value)
        assertThat(actual.participatingSharedGroup?.members)
            .extracting("accountId")
            .containsExactlyInAnyOrder(requester.accountId.value,
                                       member1OfParticipatingSharedGroup.accountId.value,
                                       member2OfParticipatingSharedGroup.accountId.value)
        assertThat(actual.participatingSharedGroup?.invitees).isEmpty()


        assertThat(actual.invitedSharedGroups)
            .extracting("sharedGroupId")
            .containsExactly(invitedSharedGroup.id.value)
        assertThat(actual.invitedSharedGroups.first().members)
            .extracting("accountId")
            .containsExactlyInAnyOrder(member1OfInvitedSharedGroup.accountId.value)
        assertThat(actual.invitedSharedGroups.first().invitees)
            .extracting("accountId")
            .containsExactly(requester.accountId.value)

        val actualUser = actual.participatingSharedGroup!!.members.find { it.accountId == requester.accountId.value }
        assertThat(actualUser).isEqualTo(JSONUser(requester.accountId.value,
                                                  requester.username.value,
                                                  requester.profileImageURL?.toURL()))
    }
}