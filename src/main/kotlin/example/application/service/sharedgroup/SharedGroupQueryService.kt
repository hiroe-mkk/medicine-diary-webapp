package example.application.service.sharedgroup

import example.application.shared.usersession.*
import example.domain.model.sharedgroup.*
import example.domain.shared.type.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional(readOnly = true)
class SharedGroupQueryService(private val sharedGroupRepository: SharedGroupRepository,
                              private val localDateTimeProvider: LocalDateTimeProvider,
                              private val sharedGroupFinder: SharedGroupFinder) {
    /**
     * 共有グループに参加しているか
     */
    fun isJoinedSharedGroup(userSession: UserSession): Boolean {
        return sharedGroupFinder.isJoinedSharedGroup(userSession.accountId)
    }

    /**
     * 招待されている共有グループの ID を取得する
     */
    fun getInvitedSharedGroupId(inviteCode: String, userSession: UserSession): SharedGroupId {
        val invitedSharedGroup = sharedGroupFinder.findInvitedSharedGroup(inviteCode, localDateTimeProvider.today())
                                 ?: throw InvalidInvitationException(inviteCode, "招待が確認できません。")
        return invitedSharedGroup.id
    }

    /**
     * 参加している共有グループの ID を取得する
     */
    fun getJoinedSharedGroup(userSession: UserSession): SharedGroupId? {
        return sharedGroupFinder.findJoinedSharedGroup(userSession.accountId)?.id
    }

    /**
     * 有効な共有グループ ID か
     */
    fun isValidSharedGroupId(sharedGroupId: SharedGroupId): Boolean {
        return sharedGroupRepository.isValidSharedGroupId(sharedGroupId)
    }
}
