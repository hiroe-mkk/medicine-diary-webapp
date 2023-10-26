package example.infrastructure.repository.profile

import example.domain.model.account.profile.*
import org.apache.ibatis.annotations.*

@Mapper
interface ProfileMapper {
    fun findOneByAccountId(accountId: String): Profile?

    fun findOneByUsername(username: String): Profile?

    fun upsertProfile(accountId: String,
                      username: String,
                      profileImageURLEndpoint: String?,
                      profileImageURLPath: String?)

    fun deleteProfile(accountId: String)
}