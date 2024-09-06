package example.testhelper.factory

import example.domain.model.sharedgroup.*
import org.apache.commons.lang3.*
import java.time.*

object SharedGroupFactory {
    fun createPendingInvitation(inviteCode: String = RandomStringUtils.randomAlphanumeric(8).uppercase(),
                                invitedOn: LocalDate = LocalDate.of(2020, 1, 1)): PendingInvitation {
        return PendingInvitation(inviteCode, invitedOn)
    }
}
