package example.application.service.sharedgroup

import example.application.shared.usersession.*
import example.domain.model.account.profile.*
import example.domain.model.sharedgroup.*
import example.domain.shared.type.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class SharedGroupInviteService(private val sharedGroupRepository: SharedGroupRepository,
                               private val profileRepository: ProfileRepository,
                               private val sharedGroupInviteEmailSender: SharedGroupInviteEmailSender,
                               private val localDateTimeProvider: LocalDateTimeProvider,
                               private val sharedGroupFinder: SharedGroupFinder) {
    /**
     * 共有グループに招待する
     */
    fun inviteToSharedGroup(sharedGroupInviteFormCommand: SharedGroupInviteFormCommand,
                            userSession: UserSession): SharedGroupId {
        val sharedGroup = sharedGroupFinder.findJoinedSharedGroup(userSession.accountId)
                          ?: SharedGroup.create(sharedGroupRepository.createSharedGroupId(),
                                                userSession.accountId)

        val pendingInvitation = PendingInvitation(sharedGroupRepository.createInviteCode(),
                                                  localDateTimeProvider.today())
        sharedGroup.invite(pendingInvitation, userSession.accountId)
        sharedGroupRepository.save(sharedGroup)

        val username = profileRepository.findByAccountId(userSession.accountId)
            ?.let { if (it.username.isDefaultValue()) null else it.username }
        val sharedGroupInviteForm = SharedGroupInviteForm(sharedGroupInviteFormCommand.validatedEmailAddress,
                                                          pendingInvitation.inviteCode,
                                                          PendingInvitation.EXPIRATION_PERIOD_TEXT,
                                                          username)
        sharedGroupInviteEmailSender.send(sharedGroupInviteForm)

        return sharedGroup.id
    }
}
