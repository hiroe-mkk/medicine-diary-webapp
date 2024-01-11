package example.application.query.user

import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.account.profile.profileimage.*

data class User(val accountId: AccountId,
                val username: Username,
                val profileImageURL: ProfileImageURL?)