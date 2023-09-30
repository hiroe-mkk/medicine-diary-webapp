package example.infrastructure.repository.profile

import example.domain.model.account.profile.*
import org.apache.ibatis.annotations.*

@Mapper
interface ProfileMapper {
    fun findOneByAccountId(accountId: String): Profile?

    fun saveProfile(accountId: String,
                    username: String,
                    profileImageRootPath: String?,
                    profileImageRelativePath: String?)

    fun deleteProfile(accountId: String)
}