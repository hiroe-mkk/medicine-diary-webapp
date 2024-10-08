package example.testhelper.factory

import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.account.profile.profileimage.*
import example.infrastructure.db.repository.shared.*

object TestAccountFactory {
    fun create(id: AccountId = AccountId(EntityIdHelper.generate()),
               credential: Credential = OAuth2Credential(IdP.GITHUB, "testSubject"),
               username: Username = Username("testUsername"),
               profileImageURL: ProfileImageURL? = null): Pair<Account, Profile> {
        val account = Account.reconstruct(id, credential)
        val profile = Profile.reconstruct(account.id, username, profileImageURL)
        return Pair(account, profile)
    }
}
