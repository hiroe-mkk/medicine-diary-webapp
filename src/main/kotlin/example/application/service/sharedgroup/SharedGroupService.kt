package example.application.service.sharedgroup

import example.application.shared.usersession.*
import example.domain.model.sharedgroup.*
import example.domain.shared.type.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class SharedGroupService(private val sharedGroupRepository: SharedGroupRepository,
                         private val localDateTimeProvider: LocalDateTimeProvider,
                         private val sharedGroupFinder: SharedGroupFinder,
                         private val sharedGroupInviteService: SharedGroupInviteService,
                         private val sharedGroupLeaveCoordinator: SharedGroupLeaveCoordinator) {
    /**
     * 共有グループに招待する
     */
    fun inviteToSharedGroup(sharedGroupInviteFormCommand: SharedGroupInviteFormCommand,
                            userSession: UserSession): SharedGroupId {
        val sharedGroup = sharedGroupFinder.findJoinedSharedGroup(userSession.accountId)
                          ?: SharedGroup.create(sharedGroupRepository.createSharedGroupId(),
                                                userSession.accountId)

        sharedGroupInviteService.invite(sharedGroup, sharedGroupInviteFormCommand, userSession.accountId)
        return sharedGroup.id
    }

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

    /**
     * 共有グループから脱退する
     */
    fun leaveSharedGroup(userSession: UserSession) {
        return sharedGroupLeaveCoordinator.leaveSharedGroupAndCloneMedicines(userSession.accountId)
    }

    /**
     * 招待されていて、なおかつ参加可能な共有グループの ID を取得する
     */
    @Transactional(readOnly = true)
    fun getJoinableInvitedSharedGroupId(inviteCode: String, userSession: UserSession): SharedGroupId {
        val invitedSharedGroup = sharedGroupFinder.findInvitedSharedGroup(inviteCode, localDateTimeProvider.today())
                                 ?: throw InvalidInvitationException(inviteCode, "招待が確認できません。")
        return invitedSharedGroup.id
    }

    /**
     * 有効なアカウント ID か
     */
    @Transactional(readOnly = true)
    fun isValidAccountId(sharedGroupId: SharedGroupId): Boolean {
        return sharedGroupRepository.isValidSharedGroupId(sharedGroupId)
    }

    /**
     * 参加している共有グループの ID を取得する
     */
    @Transactional(readOnly = true)
    fun getJoinedSharedGroup(userSession: UserSession): SharedGroupId? {
        return sharedGroupFinder.findJoinedSharedGroup(userSession.accountId)?.id
    }

    /**
     * 共有グループに参加しているか
     */
    @Transactional(readOnly = true)
    fun isJoinedSharedGroup(userSession: UserSession): Boolean {
        return sharedGroupFinder.isJoinedSharedGroup(userSession.accountId)
    }

    /**
     * 有効期限が切れた招待を削除する
     */
    fun deleteExpiredPendingInvitation() {
        sharedGroupRepository.deleteExpiredPendingInvitation()
    }
}
