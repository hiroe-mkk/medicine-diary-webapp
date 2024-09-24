package example.application.service.sharedgroup

import example.domain.model.sharedgroup.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import java.time.*

@DomainLayerTest
class SharedGroupCleanupServiceTest(@Autowired private val sharedGroupRepository: SharedGroupRepository,
                                    @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                    @Autowired private val testAccountInserter: TestAccountInserter) {
    private val sharedGroupService = SharedGroupCleanupService(sharedGroupRepository)

    @Test
    fun deleteExpiredPendingInvitation() {
        //given:
        val today = LocalDate.now()
        val pendingInvitationExpiredYesterday =
                SharedGroupFactory.createPendingInvitation(invitedOn = today.minusDays(7))
        val pendingInvitationExpiredToday =
                SharedGroupFactory.createPendingInvitation(invitedOn = today.minusDays(8))
        val pendingInvitationExpiresTomorrow =
                SharedGroupFactory.createPendingInvitation(invitedOn = today.minusDays(9))
        val accountId = testAccountInserter.insertAccountAndProfile().first.id
        val sharedGroup = testSharedGroupInserter.insert(members = setOf(accountId),
                                                         pendingInvitations = setOf(
                                                                 pendingInvitationExpiredYesterday,
                                                                 pendingInvitationExpiredToday,
                                                                 pendingInvitationExpiresTomorrow))

        //when:
        sharedGroupService.deleteExpiredPendingInvitation()

        //then:
        val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
        assertThat(foundSharedGroup?.pendingInvitations).containsExactlyInAnyOrder(pendingInvitationExpiresTomorrow)
    }
}
