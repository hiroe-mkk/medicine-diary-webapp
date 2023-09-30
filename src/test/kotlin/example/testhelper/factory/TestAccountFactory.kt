package example.testhelper.factory

import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.account.profile.profileimage.*

object TestAccountFactory {
    fun create(id: AccountId = AccountId("testAccountId"),
               credential: Credential = OAuth2Credential(IdP.GOOGLE, "testSubject"),
               username: Username = Username("testUsername"),
               profileImageFullPath: ProfileImageFullPath? = null): Pair<Account, Profile> {
        val account = Account.reconstruct(id, credential)
        val profile = Profile.reconstruct(account.id, username, profileImageFullPath)
        return Pair(account, profile)
    }
}