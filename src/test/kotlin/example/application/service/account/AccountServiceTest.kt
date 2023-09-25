package example.application.service.account

import example.domain.model.account.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@MyBatisRepositoryTest
internal class AccountServiceTest(@Autowired private val accountRepository: AccountRepository,
                                  @Autowired private val testAccountInserter: TestAccountInserter) {
    private val accountService: AccountService = AccountService(accountRepository)

    @Nested
    inner class GetOrElseCreateAccountTest {
        @Test
        @DisplayName("作成済みのアカウントを取得する")
        fun getAlreadyCreatedAccount() {
            //given:
            val account = testAccountInserter.createAndInsert()

            //when:
            val actual = accountService.findOrElseCreateAccount(account.credential)

            //then:
            assertThat(actual).usingRecursiveComparison().isEqualTo(account)
        }

        @Test
        @DisplayName("新規に作成されたアカウントを取得する")
        fun getNewlyCreatedAccount() {
            //given:
            val credential = OAuth2Credential(IdP.GOOGLE, "testSubject")

            //when:
            val actual = accountService.findOrElseCreateAccount(credential)

            //then:
            assertThat(actual.credential).isEqualTo(credential)
            // 作成されたアカウントがアカウントリポジトリに保存されている
            val foundAccount = accountRepository.findById(actual.id)
            assertThat(foundAccount).usingRecursiveComparison().isEqualTo(actual)
        }
    }
}