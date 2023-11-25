package example.domain.model.sharedgroup

import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import example.domain.model.medicine.medicineimage.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import io.mockk.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@DomainLayerTest
internal class SharedGroupUnshareServiceTest(@Autowired private val sharedGroupRepository: SharedGroupRepository,
                                             @Autowired private val medicineRepository: MedicineRepository,
                                             @Autowired private val medicationRecordRepository: MedicationRecordRepository,
                                             @Autowired private val sharedGroupUnshareService: SharedGroupUnshareService,
                                             @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                             @Autowired private val testAccountInserter: TestAccountInserter,
                                             @Autowired private val testMedicineInserter: TestMedicineInserter,
                                             @Autowired private val testMedicationRecordInserter: TestMedicationRecordInserter) {
    private lateinit var requesterAccountId: AccountId
    private lateinit var userAccountIds: List<AccountId>

    @BeforeEach
    internal fun setUp() {
        requesterAccountId = testAccountInserter.insertAccountAndProfile().first.id
        userAccountIds = listOf(testAccountInserter.insertAccountAndProfile().first.id,
                                testAccountInserter.insertAccountAndProfile().first.id)
    }

    @Test
    @DisplayName("共有を停止し、全ての招待を拒否する")
    fun unshareAndRejectAllInvitation() {
        //given:
        val participatingSharedGroup =
                testSharedGroupInserter.insert(members = setOf(requesterAccountId, userAccountIds[0]),
                                               invitees = setOf(userAccountIds[1]))
        val invitedSharedGroup = testSharedGroupInserter.insert(members = setOf(userAccountIds[1]),
                                                                invitees = setOf(requesterAccountId))

        //when:
        sharedGroupUnshareService.unshareAndRejectAllInvitation(requesterAccountId)

        //then:
        val foundParticipatedSharedGroup = sharedGroupRepository.findById(participatingSharedGroup.id)
        assertThat(foundParticipatedSharedGroup?.members).containsExactlyInAnyOrder(userAccountIds[0])
        assertThat(foundParticipatedSharedGroup?.invitees).containsExactlyInAnyOrder(userAccountIds[1])
        val foundInvitedSharedGroup = sharedGroupRepository.findById(invitedSharedGroup.id)
        assertThat(foundInvitedSharedGroup?.members).containsExactlyInAnyOrder(userAccountIds[1])
        assertThat(foundInvitedSharedGroup?.invitees).isEmpty()
    }

    @Test
    @DisplayName("共有を停止するとメンバー数が0になる場合、共有グループは削除される")
    fun membersEmptyAfterUnshare_sharedGroupIsDeleted() {
        //given:
        val participatingSharedGroup = testSharedGroupInserter.insert(members = setOf(requesterAccountId),
                                                                      invitees = setOf(userAccountIds[0]))
        val sharedGroupMedicine = testMedicineInserter.insert(MedicineOwner.create(participatingSharedGroup.id))
        val medicationRecord = testMedicationRecordInserter.insert(requesterAccountId, sharedGroupMedicine.id)

        //when:
        sharedGroupUnshareService.unshare(requesterAccountId)

        //then:
        val foundSharedGroup = sharedGroupRepository.findById(participatingSharedGroup.id)
        assertThat(foundSharedGroup).isNull()
        val foundMedicines = medicineRepository.findByOwner(participatingSharedGroup.id)
        assertThat(foundMedicines).isEmpty()
        val foundMedicationRecords = medicationRecordRepository.findById(medicationRecord.id)
        assertThat(foundMedicationRecords).isNull()
    }
}