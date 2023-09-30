package example.testhelper.inserter

import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.account.profile.profileimage.*
import org.springframework.boot.test.context.*

@TestComponent
class TestAccountInserter(private val accountRepository: AccountRepository,
                          private val profileRepository: ProfileRepository) {
    private var num: Int = 1

    /**
     * テスト用のアカウントとプロフィールを生成して、リポジトリに保存する
     */
    fun insertAccountAndProfile(id: AccountId = AccountId("testAccountId$num"),
                                credential: Credential = OAuth2Credential(IdP.GOOGLE,
                                                                          "testSubject${num++}"),
                                username: Username = Username("testUsername"),
                                profileImageFullPath: ProfileImageFullPath? = null): Pair<Account, Profile> {
        val account = Account.reconstruct(id, credential)
        val profile = Profile.reconstruct(account.id, username, profileImageFullPath)
        accountRepository.save(account)
        profileRepository.save(profile)
        return Pair(account, profile)
    }
}