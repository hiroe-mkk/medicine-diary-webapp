package example.application.service.account

import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.profile.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@MyBatisRepositoryTest
internal class AccountServiceTest(@Autowired private val accountRepository: AccountRepository,
                                  @Autowired private val profileRepository: ProfileRepository,
                                  @Autowired private val testAccountInserter: TestAccountInserter) {
    private val accountService: AccountService = AccountService(accountRepository, profileRepository)

    @Nested
    inner class GetOrElseCreateAccountTest {
        @Test
        @DisplayName("作成済みのアカウントを取得する")
        fun getAlreadyCreatedAccount() {
            //given:
            val (account, _) = testAccountInserter.createAndInsert()

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
            val expected = Account.reconstruct(actual.id, credential)
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
            // 作成されたアカウントが保存されている
            val foundAccount = accountRepository.findById(actual.id)
            assertThat(foundAccount).usingRecursiveComparison().isEqualTo(expected)
            // 作成されたプロフィールが保存されている
            val foundProfile = profileRepository.findByAccountId(actual.id)
            val expectedProfile = Profile(actual.id, Username(""))
            assertThat(foundProfile).usingRecursiveComparison().isEqualTo(expectedProfile)
        }
    }
}