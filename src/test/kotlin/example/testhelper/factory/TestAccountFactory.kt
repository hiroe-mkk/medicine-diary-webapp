package example.testhelper.factory

import example.domain.model.account.*

object TestAccountFactory {
    fun create(id: AccountId = AccountId("testAccountId"),
               credential: Credential = OAuth2Credential(IdP.GOOGLE, "testSubject")): Account {
        return Account(id, credential)
    }
}