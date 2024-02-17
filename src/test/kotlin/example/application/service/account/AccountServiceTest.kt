package example.application.service.account

import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import example.domain.model.sharedgroup.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@DomainLayerTest
internal class AccountServiceTest(@Autowired private val accountRepository: AccountRepository,
                                  @Autowired private val profileRepository: ProfileRepository,
                                  @Autowired private val sharedGroupRepository: SharedGroupRepository,
                                  @Autowired private val medicineRepository: MedicineRepository,
                                  @Autowired private val medicationRecordRepository: MedicationRecordRepository,
                                  @Autowired private val sharedGroupUnshareService: SharedGroupUnshareService,
                                  @Autowired private val medicineDeletionService: MedicineDeletionService,
                                  @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                  @Autowired private val testAccountInserter: TestAccountInserter,
                                  @Autowired private val testMedicineInserter: TestMedicineInserter,
                                  @Autowired private val testMedicationRecordInserter: TestMedicationRecordInserter) {
    private val accountService: AccountService = AccountService(accountRepository,
                                                                profileRepository,
                                                                medicationRecordRepository,
                                                                sharedGroupUnshareService,
                                                                medicineDeletionService)

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
        testSharedGroupInserter.insert(members = setOf(requesterAccountId))
        testSharedGroupInserter.insert(members = setOf(testAccountInserter.insertAccountAndProfile().first.id),
                                       invitees = setOf(requesterAccountId))
        val medicine = testMedicineInserter.insert(MedicineOwner.create(requesterAccountId))
        val medicationRecord = testMedicationRecordInserter.insert(requesterAccountId, medicine.id)


        //when:
        accountService.deleteAccount(userSession)

        //then:
        val foundAccount = accountRepository.findById(userSession.accountId)
        assertThat(foundAccount).isNull()
        val foundProfile = profileRepository.findByAccountId(userSession.accountId)
        assertThat(foundProfile).isNull()
        val foundParticipatingSharedGroup = sharedGroupRepository.findByMember(requesterAccountId)
        assertThat(foundParticipatingSharedGroup).isNull()
        val foundInvitedSharedGroup = sharedGroupRepository.findByInvitee(requesterAccountId)
        assertThat(foundInvitedSharedGroup).isEmpty()
        val foundMedicine = medicineRepository.findById(medicine.id)
        assertThat(foundMedicine).isNull()
        val foundMedicationRecord = medicationRecordRepository.findById(medicationRecord.id)
        assertThat(foundMedicationRecord).isNull()
    }
}