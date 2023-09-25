package example.testhelper.inserter

import example.domain.model.account.*
import org.springframework.boot.test.context.*

@TestComponent
class TestAccountInserter(private val accountRepository: AccountRepository) {
    private var num: Int = 1

    /**
     * テスト用のアカウントを生成して、リポジトリに保存する
     */
    fun createAndInsert(id: AccountId = AccountId("testAccountId$num"),
                        credential: Credential = OAuth2Credential(IdP.GOOGLE, "testSubject${num++}")): Account {
        val account = Account(id, credential)
        accountRepository.save(account)
        return account
    }
}