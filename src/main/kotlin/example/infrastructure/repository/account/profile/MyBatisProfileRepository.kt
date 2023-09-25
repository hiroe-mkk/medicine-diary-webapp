package example.infrastructure.repository.account.profile

import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.profile.*
import org.springframework.stereotype.*

@Repository
class MyBatisProfileRepository(private val profileMapper: ProfileMapper) : ProfileRepository {
    override fun findByAccountId(accountId: AccountId): Profile? {
        return profileMapper.findOneByAccountId(accountId.value)
    }

    override fun save(profile: Profile) {
        profileMapper.saveProfile(profile.accountId.value, profile.username.value)
    }
}