package example.infrastructure.repository.account.profile

import example.domain.model.account.*
import example.domain.model.profile.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@MyBatisRepositoryTest
internal class MyBatisProfileRepositoryTest(@Autowired private val profileRepository: ProfileRepository,
                                            @Autowired private val accountRepository: AccountRepository,
                                            @Autowired private val testAccountInserter: TestAccountInserter) {
    @Test
    fun afterSavingProfile_canFindByAccountId() {
        //given:
        val (account, profile) = TestAccountFactory.create()
        accountRepository.save(account)

        //when:
        profileRepository.save(profile)
        val foundProfile = profileRepository.findByAccountId(account.id)

        //then:
        assertThat(foundProfile).usingRecursiveComparison().isEqualTo(profile)
    }

    @Test
    fun canDeleteProfile() {
        //given:
        val (account, _) = testAccountInserter.insertAccountAndProfile()

        //when:
        profileRepository.delete(account.id)

        //then:
        val foundProfile = profileRepository.findByAccountId(account.id)
        assertThat(foundProfile).isNull()
    }
}