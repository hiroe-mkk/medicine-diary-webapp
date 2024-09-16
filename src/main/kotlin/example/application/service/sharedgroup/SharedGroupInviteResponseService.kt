package example.application.service.sharedgroup

import example.application.shared.usersession.*
import example.domain.model.sharedgroup.*
import example.domain.shared.type.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class SharedGroupInviteResponseService(private val sharedGroupRepository: SharedGroupRepository,
                                       private val localDateTimeProvider: LocalDateTimeProvider,
                                       private val sharedGroupFinder: SharedGroupFinder) {
    /**
     * 共有グループに参加する
     */
    fun joinSharedGroup(inviteCode: String, userSession: UserSession) {
        if (sharedGroupFinder.isJoinedSharedGroup(userSession.accountId))
            throw SharedGroupJoinFailedException("参加できる共有グループは1つまでです。")

        val today = localDateTimeProvider.today()
        val invitedSharedGroup = sharedGroupFinder.findInvitedSharedGroup(inviteCode, today)
                                 ?: throw SharedGroupJoinFailedException("現在、このグループからは招待されていません。")

        invitedSharedGroup.join(inviteCode, userSession.accountId, today)
        sharedGroupRepository.save(invitedSharedGroup)
    }

    /**
     * 共有グループへの招待を拒否する
     */
    fun rejectInvitationToSharedGroup(inviteCode: String, userSession: UserSession) {
        val invitedSharedGroup = sharedGroupFinder.findInvitedSharedGroup(inviteCode,
                                                                          localDateTimeProvider.today()) ?: return

        invitedSharedGroup.reject(inviteCode)
        sharedGroupRepository.save(invitedSharedGroup)
    }
}
