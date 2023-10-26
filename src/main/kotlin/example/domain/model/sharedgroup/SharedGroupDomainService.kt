package example.domain.model.sharedgroup

import example.application.service.profile.*
import example.domain.model.account.*
import example.domain.model.account.profile.*
import org.springframework.stereotype.*

@Component
class SharedGroupDomainService(private val sharedGroupRepository: SharedGroupRepository,
                               private val profileRepository: ProfileRepository) {
    fun isShareableState(accountId: AccountId): Boolean {
        return isParticipatingInSharedGroup(accountId) || isUsernameEmpty(accountId)
    }

    fun requireShareableState(accountId: AccountId) {
        if (isParticipatingInSharedGroup(accountId)) throw ShareFailedException("参加できる共有グループは1つまでです。")
        if (isUsernameEmpty(accountId)) throw ShareFailedException("ユーザー名が設定されていません。")
    }

    fun requireParticipationPossibleState(invitee: AccountId) {
        if (isParticipatingInSharedGroup(invitee)) throw ParticipationInSharedGroupFailedException("参加できる共有グループは1つまでです。")
        if (isUsernameEmpty(invitee)) throw ParticipationInSharedGroupFailedException("ユーザー名が設定されていません。")
    }

    private fun isParticipatingInSharedGroup(accountId: AccountId): Boolean {
        return sharedGroupRepository.findByMember(accountId) != null
    }

    private fun isUsernameEmpty(accountId: AccountId): Boolean {
        val profile = profileRepository.findByAccountId(accountId) ?: throw ProfileNotFoundException(accountId)
        return profile.isUsernameEmpty()
    }
}