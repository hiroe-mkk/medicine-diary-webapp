package example.application.service.account

import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@DomainLayerTest
class AccountAuthenticationServiceTest(@Autowired private val accountRepository: AccountRepository,
                                       @Autowired private val profileRepository: ProfileRepository,
                                       @Autowired private val testAccountInserter: TestAccountInserter) {
    private val accountAuthenticationService = AccountAuthenticationService(accountRepository, profileRepository)

    @Test
    @DisplayName("作成済みのアカウントを取得する")
    fun getAlreadyCreatedAccount() {
        //given:
        val (requesterAccount, _) = testAccountInserter.insertAccountAndProfile()

        //when:
        val actual = accountAuthenticationService.findOrElseCreateAccount(requesterAccount.credential)

        //then:
        assertThat(actual).usingRecursiveComparison().isEqualTo(requesterAccount)
    }

    @Test
    @DisplayName("新規に作成されたアカウントを取得する")
    fun getNewlyCreatedAccount() {
        //given:
        val credential = OAuth2Credential(IdP.GITHUB, "testSubject")

        //when:
        val actual = accountAuthenticationService.findOrElseCreateAccount(credential)

        //then:
        val expected = Account.reconstruct(actual.id, credential)
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        val foundAccount = accountRepository.findById(actual.id)
        assertThat(foundAccount).usingRecursiveComparison().isEqualTo(expected)
        val foundProfile = profileRepository.findByAccountId(actual.id)
        val expectedProfile = Profile.reconstruct(actual.id, Username(""), null)
        assertThat(foundProfile).usingRecursiveComparison().isEqualTo(expectedProfile)
    }
}
