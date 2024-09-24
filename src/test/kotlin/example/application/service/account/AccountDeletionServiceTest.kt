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
class AccountDeletionServiceTest(@Autowired private val accountRepository: AccountRepository,
                                 @Autowired private val profileRepository: ProfileRepository,
                                 @Autowired private val sharedGroupRepository: SharedGroupRepository,
                                 @Autowired private val medicineRepository: MedicineRepository,
                                 @Autowired private val medicationRecordRepository: MedicationRecordRepository,
                                 @Autowired private val sharedGroupLeaveCoordinator: SharedGroupLeaveCoordinator,
                                 @Autowired private val medicineDeletionCoordinator: MedicineDeletionCoordinator,
                                 @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                 @Autowired private val testAccountInserter: TestAccountInserter,
                                 @Autowired private val testMedicineInserter: TestMedicineInserter,
                                 @Autowired private val testMedicationRecordInserter: TestMedicationRecordInserter) {
    private val accountDeletionService = AccountDeletionService(accountRepository,
                                                                profileRepository,
                                                                medicationRecordRepository,
                                                                sharedGroupLeaveCoordinator,
                                                                medicineDeletionCoordinator)

    @Test
    @DisplayName("アカウントを削除する")
    fun deleteAccount() {
        //given:
        val requesterAccountId = testAccountInserter.insertAccountAndProfile().first.id
        val userSession = UserSessionFactory.create(requesterAccountId)
        testSharedGroupInserter.insert(members = setOf(requesterAccountId))
        testSharedGroupInserter.insert(members = setOf(testAccountInserter.insertAccountAndProfile().first.id),
                                       pendingInvitations = setOf(
                                               SharedGroupFactory.createPendingInvitation()))
        val medicine = testMedicineInserter.insert(MedicineOwner.create(requesterAccountId))
        val medicationRecord = testMedicationRecordInserter.insert(requesterAccountId, medicine.id)


        //when:
        accountDeletionService.deleteAccount(userSession)

        //then:
        val foundAccount = accountRepository.findById(userSession.accountId)
        assertThat(foundAccount).isNull()
        val foundProfile = profileRepository.findByAccountId(userSession.accountId)
        assertThat(foundProfile).isNull()
        val foundJoinedSharedGroup = sharedGroupRepository.findByMember(requesterAccountId)
        assertThat(foundJoinedSharedGroup).isNull()
        val foundMedicine = medicineRepository.findById(medicine.id)
        assertThat(foundMedicine).isNull()
        val foundMedicationRecord = medicationRecordRepository.findById(medicationRecord.id)
        assertThat(foundMedicationRecord).isNull()
    }
}
