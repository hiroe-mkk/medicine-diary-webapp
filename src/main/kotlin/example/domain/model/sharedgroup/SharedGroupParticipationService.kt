package example.domain.model.sharedgroup

import example.domain.model.account.*
import example.domain.model.account.profile.*
import org.springframework.stereotype.*

@Component
class SharedGroupParticipationService(private val sharedGroupQueryService: SharedGroupQueryService,
                                      private val profileRepository: ProfileRepository) {
    fun requireSharePossible(accountId: AccountId) {
        if (isParticipatingInSharedGroup(accountId)) throw ShareException("参加できる共有グループは1つまでです。")
        if (isUsernameDefaultValue(accountId)) throw ShareException("ユーザー名が設定されていません。")
    }

    fun requireParticipationPossible(invitee: AccountId) {
        if (isParticipatingInSharedGroup(invitee)) throw ParticipationInSharedGroupException("参加できる共有グループは1つまでです。")
        if (isUsernameDefaultValue(invitee)) throw ParticipationInSharedGroupException("ユーザー名が設定されていません。")
    }

    private fun isParticipatingInSharedGroup(accountId: AccountId): Boolean {
        return sharedGroupQueryService.isParticipatingInSharedGroup(accountId)
    }

    private fun isUsernameDefaultValue(accountId: AccountId): Boolean {
        val profile = profileRepository.findByAccountId(accountId) ?: return false
        return profile.username.isDefaultValue()
    }
}