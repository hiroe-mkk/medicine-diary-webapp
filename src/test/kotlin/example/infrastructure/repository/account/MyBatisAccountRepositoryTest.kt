package example.infrastructure.repository.account

import example.domain.model.account.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.mybatis.spring.boot.test.autoconfigure.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.test.autoconfigure.jdbc.*

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 組み込みデータベースへの置き換えを無効化する
internal class MyBatisAccountRepositoryTest(@Autowired private val accountMapper: AccountMapper) {
    private val accountRepository: AccountRepository = MyBatisAccountRepository(accountMapper)

    @Test
    fun afterSavingAccount_canFindById() {
        //given:
        val account = Account(AccountId("testAccountId"),
                              OAuth2Credential(IdP.GOOGLE, "testSubject"),
                              Username("testUsername"))

        //when:
        accountRepository.save(account)
        val foundAccount = accountRepository.findById(account.id)

        //then:
        assertThat(foundAccount).usingRecursiveComparison().isEqualTo(account)
    }
}