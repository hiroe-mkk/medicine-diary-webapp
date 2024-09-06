package example.domain.model.sharedgroup

import example.domain.model.account.*
import example.domain.model.account.profile.*
import org.springframework.stereotype.*

@Component
class SharedGroupJoinService(private val sharedGroupQueryService: SharedGroupQueryService,
                             private val profileRepository: ProfileRepository) {
    fun requireJoinPossible(invitee: AccountId) {
        if (isJoinedSharedGroup(invitee)) throw SharedGroupJoinFailedException("参加できる共有グループは1つまでです。")
        if (isUsernameDefaultValue(invitee)) throw SharedGroupJoinFailedException("ユーザー名が設定されていません。")
    }

    private fun isJoinedSharedGroup(accountId: AccountId): Boolean {
        return sharedGroupQueryService.isJoinedSharedGroup(accountId)
    }

    private fun isUsernameDefaultValue(accountId: AccountId): Boolean {
        val profile = profileRepository.findByAccountId(accountId) ?: return false
        return profile.username.isDefaultValue()
    }
}
