package example.domain.model.sharedgroup

import example.application.service.profile.*
import example.domain.model.account.*
import example.domain.model.account.profile.*
import org.springframework.stereotype.*

@Component
class SharedGroupDomainService(private val sharedGroupRepository: SharedGroupRepository,
                               private val profileRepository: ProfileRepository) {
    fun canShareRequest(requester: AccountId): Boolean {
        return isParticipatingInSharedGroup(requester) || isUsernameEmpty(requester)
    }

    fun requireShareRequestPossibleState(requester: AccountId) {
        if (isParticipatingInSharedGroup(requester)) throw ShareRequestFailedException("参加できる共有グループは1つまでです。")
        if (isUsernameEmpty(requester)) throw ShareRequestFailedException("ユーザー名が設定されていません。")
    }

    fun requireParticipationPossibleState(invitee: AccountId) {
        if (isParticipatingInSharedGroup(invitee)) throw ParticipationInSharedGroupFailedException("参加できる共有グループは1つまでです。")
        if (isUsernameEmpty(invitee)) throw ParticipationInSharedGroupFailedException("ユーザー名が設定されていません。")
    }

    private fun isParticipatingInSharedGroup(accountId: AccountId): Boolean {
        val result = sharedGroupRepository.findByAccountId(accountId)
            .filter { it.isParticipatingIn(accountId) }
        return result.isNotEmpty()
    }

    private fun isUsernameEmpty(accountId: AccountId): Boolean {
        val profile = profileRepository.findByAccountId(accountId) ?: throw ProfileNotFoundException(accountId)
        return profile.isUsernameEmpty()
    }
}