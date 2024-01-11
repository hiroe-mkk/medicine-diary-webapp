package example.infrastructure.repository.profile

import example.domain.model.account.*
import example.domain.model.account.profile.*
import org.springframework.stereotype.*

@Repository
class MyBatisProfileRepository(private val profileMapper: ProfileMapper) : ProfileRepository {
    override fun findByAccountId(accountId: AccountId): Profile? {
        return profileMapper.findOneByAccountId(accountId.value)
    }

    override fun findByUsername(username: Username): Profile? {
        if (username.isDefaultValue()) return null
        return profileMapper.findOneByUsername(username.value)
    }

    override fun save(profile: Profile) {
        profileMapper.upsertProfile(profile.accountId.value,
                                    profile.username.value,
                                    profile.profileImageURL?.endpoint,
                                    profile.profileImageURL?.path)
    }

    override fun deleteByAccountId(accountId: AccountId) {
        profileMapper.deleteProfile(accountId.value)
    }
}