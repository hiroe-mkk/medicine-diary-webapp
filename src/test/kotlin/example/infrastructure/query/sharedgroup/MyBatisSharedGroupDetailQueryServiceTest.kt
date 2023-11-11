package example.infrastructure.query.sharedgroup

import example.application.query.shared.type.*
import example.application.query.sharedgroup.*
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
internal class MyBatisSharedGroupDetailQueryServiceTest(@Autowired private val sharedGroupQueryService: SharedGroupQueryService,
                                                        @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                                        @Autowired private val testAccountInserter: TestAccountInserter) {
    @Test
    @DisplayName("共有グループを取得する")
    fun getSharedGroups() {
        //given:
        val user = testAccountInserter.insertAccountAndProfile().second
        val userSession = UserSessionFactory.create(user.accountId)

        val (_, participatingSharedGroupMember1) = testAccountInserter.insertAccountAndProfile()
        val (_, participatingSharedGroupMember2) = testAccountInserter.insertAccountAndProfile()
        val participatingSharedGroup =
                testSharedGroupInserter.insert(members = setOf(user.accountId,
                                                               participatingSharedGroupMember1.accountId,
                                                               participatingSharedGroupMember2.accountId),
                                               invitees = emptySet())

        val (_, invitedSharedGroupMember1) = testAccountInserter.insertAccountAndProfile()
        val (_, invitedSharedGroupMember2) = testAccountInserter.insertAccountAndProfile()
        val invitedSharedGroup = testSharedGroupInserter.insert(members = setOf(invitedSharedGroupMember1.accountId,
                                                                                invitedSharedGroupMember2.accountId),
                                                                invitees = setOf(userSession.accountId))

        //when:
        val actual = sharedGroupQueryService.findSharedGroupDetails(userSession)

        //then:
        assertThat(actual.participatingSharedGroup?.sharedGroupId).isEqualTo(participatingSharedGroup.id)
        assertThat(actual.participatingSharedGroup?.members)
            .extracting("accountId")
            .containsExactlyInAnyOrder(user.accountId,
                                       participatingSharedGroupMember1.accountId,
                                       participatingSharedGroupMember2.accountId)
        assertThat(actual.participatingSharedGroup?.invitees).isEmpty()


        assertThat(actual.invitedSharedGroups)
            .extracting("sharedGroupId")
            .containsExactly(invitedSharedGroup.id)
        assertThat(actual.invitedSharedGroups.first().members)
            .extracting("accountId")
            .containsExactlyInAnyOrder(invitedSharedGroupMember1.accountId,
                                       invitedSharedGroupMember2.accountId)
        assertThat(actual.invitedSharedGroups.first().invitees)
            .extracting("accountId")
            .containsExactly(user.accountId)

        val actualUser = actual.participatingSharedGroup!!.members.find { it.accountId == user.accountId }
        assertThat(actualUser).isEqualTo(User(user.accountId,
                                              user.username,
                                              user.profileImageURL))
    }
}