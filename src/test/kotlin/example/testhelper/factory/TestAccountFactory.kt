package example.testhelper.factory

import example.domain.model.account.*
import example.domain.model.account.profile.*

object TestAccountFactory {
    fun create(id: AccountId = AccountId("testAccountId"),
               credential: Credential = OAuth2Credential(IdP.GOOGLE, "testSubject"),
               username: Username = Username("testUsername")): Pair<Account, Profile> {
        val account = Account.reconstruct(id, credential)
        val profile = Profile(account.id, username)
        return Pair(account, profile)
    }
}