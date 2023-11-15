package example.application.service.account

import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@MyBatisRepositoryTest
internal class AccountServiceTest(@Autowired private val accountRepository: AccountRepository,
                                  @Autowired private val profileRepository: ProfileRepository,
                                  @Autowired private val medicineRepository: MedicineRepository,
                                  @Autowired private val takingRecordRepository: TakingRecordRepository,
                                  @Autowired private val testAccountInserter: TestAccountInserter,
                                  @Autowired private val testMedicineInserter: TestMedicineInserter,
                                  @Autowired private val testTakingRecordInserter: TestTakingRecordInserter) {
    private val accountService: AccountService =
            AccountService(accountRepository, profileRepository, takingRecordRepository)

    @Nested
    inner class GetOrElseCreateAccountTest {
        @Test
        @DisplayName("作成済みのアカウントを取得する")
        fun getAlreadyCreatedAccount() {
            //given:
            val (requesterAccount, _) = testAccountInserter.insertAccountAndProfile()

            //when:
            val actual = accountService.findOrElseCreateAccount(requesterAccount.credential)

            //then:
            assertThat(actual).usingRecursiveComparison().isEqualTo(requesterAccount)
        }

        @Test
        @DisplayName("新規に作成されたアカウントを取得する")
        fun getNewlyCreatedAccount() {
            //given:
            val credential = OAuth2Credential(IdP.GITHUB, "testSubject")

            //when:
            val actual = accountService.findOrElseCreateAccount(credential)

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

    @Test
    @DisplayName("アカウントを削除する")
    fun deleteAccount() {
        //given:
        val requesterAccountId = testAccountInserter.insertAccountAndProfile().first.id
        val userSession = UserSessionFactory.create(requesterAccountId)
        val medicine = testMedicineInserter.insert(MedicineOwner.create(requesterAccountId))
        val takingRecord = testTakingRecordInserter.insert(requesterAccountId, medicine.id)

        //when:
        accountService.deleteAccount(userSession)

        //then:
        val foundAccount = accountRepository.findById(userSession.accountId)
        assertThat(foundAccount).isNull()
        val foundProfile = profileRepository.findByAccountId(userSession.accountId)
        assertThat(foundProfile).isNull()
        val foundTakingRecord = takingRecordRepository.findById(takingRecord.id)
        assertThat(foundTakingRecord).isNull()
    }
}