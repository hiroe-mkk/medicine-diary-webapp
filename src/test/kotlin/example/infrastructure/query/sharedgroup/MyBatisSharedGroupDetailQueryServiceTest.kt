package example.infrastructure.query.sharedgroup

import example.application.query.shared.type.*
import example.application.query.sharedgroup.*
import example.domain.model.account.profile.profileimage.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
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
        val (userAccount, userProfile) = testAccountInserter.insertAccountAndProfile()
        val userSession = UserSessionFactory.create(userAccount.id)
        val (anotherUser1Account, anotherUser1Profile) = testAccountInserter.insertAccountAndProfile()
        val participatingSharedGroup = testSharedGroupInserter.insert(members = setOf(userAccount.id,
                                                                                      anotherUser1Account.id))
        val (anotherUser2Account, anotherUser2Profile) = testAccountInserter.insertAccountAndProfile()
        val invitedSharedGroup = testSharedGroupInserter.insert(members = setOf(anotherUser2Account.id),
                                                                invitees = setOf(userSession.accountId))

        //when:
        val actual = sharedGroupQueryService.findSharedGroupDetails(userSession)

        //then:
        assertThat(actual.participatingSharedGroup?.sharedGroupId).isEqualTo(participatingSharedGroup.id)
        assertThat(actual.participatingSharedGroup?.members).containsExactlyInAnyOrder(User(userProfile.accountId,
                                                                                            userProfile.username,
                                                                                            userProfile.profileImageURL),
                                                                                       User(anotherUser1Profile.accountId,
                                                                                            anotherUser1Profile.username,
                                                                                            anotherUser1Profile.profileImageURL))
        assertThat(actual.participatingSharedGroup?.invitees).isEmpty()
        assertThat(actual.invitedSharedGroups)
            .extracting("sharedGroupId")
            .containsExactlyInAnyOrder(invitedSharedGroup.id)
        val expectedInvitedSharedGroup = actual.invitedSharedGroups.find { it.sharedGroupId == invitedSharedGroup.id }!!
        assertThat(expectedInvitedSharedGroup.members).containsExactlyInAnyOrder(User(anotherUser2Profile.accountId,
                                                                                      anotherUser2Profile.username,
                                                                                      anotherUser2Profile.profileImageURL))
        assertThat(expectedInvitedSharedGroup.invitees).containsExactlyInAnyOrder(User(userProfile.accountId,
                                                                                       userProfile.username,
                                                                                       userProfile.profileImageURL))
    }
}