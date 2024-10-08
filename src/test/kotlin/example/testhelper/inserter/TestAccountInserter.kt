package example.testhelper.inserter

import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.account.profile.profileimage.*
import example.infrastructure.db.repository.shared.*
import org.springframework.boot.test.context.*

@TestComponent
class TestAccountInserter(private val accountRepository: AccountRepository,
                          private val profileRepository: ProfileRepository) {
    private var num: Int = 1

    /**
     * テスト用のアカウントとプロフィールを生成して、リポジトリに保存する
     */
    fun insertAccountAndProfile(id: AccountId = AccountId(EntityIdHelper.generate()),
                                credential: Credential = OAuth2Credential(IdP.GITHUB,
                                                                          "testSubject${num++}"),
                                username: Username = Username("testUsername"),
                                profileImageURL: ProfileImageURL? = null): Pair<Account, Profile> {
        val account = Account.reconstruct(id, credential)
        val profile = Profile.reconstruct(account.id, username, profileImageURL)
        accountRepository.save(account)
        profileRepository.save(profile)
        return Pair(account, profile)
    }
}
