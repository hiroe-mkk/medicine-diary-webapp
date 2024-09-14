package example.application.service.sharedgroup

import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.sharedgroup.*
import example.domain.shared.type.*
import example.infrastructure.shared.*
import org.springframework.stereotype.*

@Component
class SharedGroupInviteService(private val sharedGroupRepository: SharedGroupRepository,
                               private val sharedGroupInviteEmailSender: SharedGroupInviteEmailSender,
                               private val localDateTimeProvider: LocalDateTimeProvider,
                               private val profileRepository: ProfileRepository,
                               private val applicationProperties: ApplicationProperties) {
    fun invite(sharedGroup: SharedGroup,
               sharedGroupInviteFormCommand: SharedGroupInviteFormCommand,
               inviter: AccountId) {
        val pendingInvitation = PendingInvitation(sharedGroupRepository.createInviteCode(),
                                                  localDateTimeProvider.today())
        sharedGroup.invite(pendingInvitation, inviter)
        sharedGroupRepository.save(sharedGroup)

        val username = profileRepository.findByAccountId(inviter)
            ?.let { if (it.username.isDefaultValue()) null else it.username }
        val sharedGroupInviteForm = SharedGroupInviteForm(sharedGroupInviteFormCommand.validatedEmailAddress,
                                                          createInviteLink(pendingInvitation.inviteCode),
                                                          PendingInvitation.EXPIRATION_PERIOD_TEXT,
                                                          username)
        sharedGroupInviteEmailSender.send(sharedGroupInviteForm)
    }

    private fun createInviteLink(inviteCode: String): String {
        return "${applicationProperties.endpoint.web}/shared-group/join?code=${inviteCode}"
    }
}
