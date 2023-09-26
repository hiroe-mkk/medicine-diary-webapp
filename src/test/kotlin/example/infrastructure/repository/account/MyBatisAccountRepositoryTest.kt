package example.infrastructure.repository.account

import example.domain.model.account.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@MyBatisRepositoryTest
internal class MyBatisAccountRepositoryTest(@Autowired private val accountRepository: AccountRepository,
                                            @Autowired private val testAccountInserter: TestAccountInserter) {
    @Test
    fun afterSavingAccount_canFindById() {
        //given:
        val account = Account.reconstruct(AccountId("testAccountId"),
                                          OAuth2Credential(IdP.GOOGLE, "testSubject"))

        //when:
        accountRepository.save(account)
        val foundAccount = accountRepository.findById(account.id)

        //then:
        assertThat(foundAccount).usingRecursiveComparison().isEqualTo(account)
    }

    @Test
    fun findByCredential() {
        //given:
        val (account, _) = testAccountInserter.insertAccountAndProfile()

        //when:
        val actual = accountRepository.findByCredential(account.credential)

        //then:
        assertThat(actual).usingRecursiveComparison().isEqualTo(account)
    }
}