package example.testhelper.factory

import example.domain.model.account.*

object TestAccountFactory {
    fun create(id: AccountId = AccountId("testAccountId"),
               credential: Credential = OAuth2Credential(IdP.GOOGLE, "testSubject"),
               username: Username = Username("testUsername")): Account {
        return Account.reconstruct(id, credential, username)
    }
}