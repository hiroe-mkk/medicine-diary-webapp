package example.domain.model.account.profile

import example.domain.model.account.*

interface ProfileRepository {
    fun findByAccountId(accountId: AccountId): Profile?

    fun findByUsername(username: Username): Profile?

    fun save(profile: Profile)

    fun deleteByAccountId(accountId: AccountId)
}