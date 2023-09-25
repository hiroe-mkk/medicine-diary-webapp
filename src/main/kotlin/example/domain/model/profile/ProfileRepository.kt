package example.domain.model.profile

import example.domain.model.account.*
import example.domain.model.account.profile.*

interface ProfileRepository {
    fun findByAccountId(accountId: AccountId): Profile?

    fun save(profile: Profile)
}