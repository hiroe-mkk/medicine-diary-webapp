package example.domain.model.sharedgroup

import example.application.service.profile.*
import example.domain.model.account.*
import example.domain.model.account.profile.*
import org.springframework.stereotype.*

@Component
class SharedGroupDomainService(private val sharedGroupRepository: SharedGroupRepository,
                               private val profileRepository: ProfileRepository) {
    fun requireShareableState(accountId: AccountId) {
        if (isParticipatingInSharedGroup(accountId)) throw ShareException("参加できる共有グループは1つまでです。")
        if (isUsernameDefaultValue(accountId)) throw ShareException("ユーザー名が設定されていません。")
    }

    fun requireParticipationPossibleState(invitee: AccountId) {
        if (isParticipatingInSharedGroup(invitee)) throw ParticipationInSharedGroupException("参加できる共有グループは1つまでです。")
        if (isUsernameDefaultValue(invitee)) throw ParticipationInSharedGroupException("ユーザー名が設定されていません。")
    }

    fun isParticipatingInSharedGroup(accountId: AccountId): Boolean {
        return sharedGroupRepository.findByMember(accountId) != null
    }

    private fun isUsernameDefaultValue(accountId: AccountId): Boolean {
        val profile = profileRepository.findByAccountId(accountId) ?: throw ProfileNotFoundException(accountId)
        return profile.username.isDefaultValue()
    }
}