package example.domain.model.sharedgroup

import example.domain.model.account.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@DomainLayerTest
internal class SharedGroupLeaveServiceTest(@Autowired private val sharedGroupRepository: SharedGroupRepository,
                                           @Autowired private val medicineRepository: MedicineRepository,
                                           @Autowired private val medicationRecordRepository: MedicationRecordRepository,
                                           @Autowired private val sharedGroupLeaveService: SharedGroupLeaveService,
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
    @DisplayName("共有グループから脱退し、全ての招待を拒否する")
    fun leaveSharedGroupAndRejectAllInvitation() {
        //given:
        val joinedSharedGroup =
                testSharedGroupInserter.insert(members = setOf(requesterAccountId, userAccountIds[0]),
                                               invitees = setOf(userAccountIds[1]))
        val invitedSharedGroup = testSharedGroupInserter.insert(members = setOf(userAccountIds[1]),
                                                                invitees = setOf(requesterAccountId))

        //when:
        sharedGroupLeaveService.leaveAndRejectAllInvitation(requesterAccountId)

        //then:
        val foundLeftSharedGroup = sharedGroupRepository.findById(joinedSharedGroup.id)
        assertThat(foundLeftSharedGroup?.members).containsExactlyInAnyOrder(userAccountIds[0])
        assertThat(foundLeftSharedGroup?.invitees).containsExactlyInAnyOrder(userAccountIds[1])
        val foundInvitedSharedGroup = sharedGroupRepository.findById(invitedSharedGroup.id)
        assertThat(foundInvitedSharedGroup?.members).containsExactlyInAnyOrder(userAccountIds[1])
        assertThat(foundInvitedSharedGroup?.invitees).isEmpty()
    }

    @Test
    @DisplayName("薬を複製し、共有グループから脱退する")
    fun leaveAndCloneMedicines() {
        //given:
        val joinedSharedGroup =
                testSharedGroupInserter.insert(members = setOf(requesterAccountId, userAccountIds[0]))
        val sharedGroupMedicine = testMedicineInserter.insert(MedicineOwner.create(joinedSharedGroup.id))
        val medicationRecord = testMedicationRecordInserter.insert(requesterAccountId, sharedGroupMedicine.id)

        //when:
        sharedGroupLeaveService.leaveAndCloneMedicines(requesterAccountId)

        //then:
        val foundSharedGroup = sharedGroupRepository.findById(joinedSharedGroup.id)
        assertThat(foundSharedGroup?.members).containsExactly(userAccountIds[0])
        val foundSharedGroupMedicine = medicineRepository.findById(sharedGroupMedicine.id)
        assertThat(foundSharedGroupMedicine).usingRecursiveComparison().isEqualTo(sharedGroupMedicine)
        val foundOwnedMedicine = medicineRepository.findByOwner(requesterAccountId).first()
        assertThat(foundOwnedMedicine)
            .usingRecursiveComparison()
            .ignoringFields("id", "owner")
            .isEqualTo(foundSharedGroupMedicine)
        val foundMedicationRecord = medicationRecordRepository.findById(medicationRecord.id)
        assertThat(foundMedicationRecord?.takenMedicine).isEqualTo(foundOwnedMedicine.id)
    }

    @Test
    @DisplayName("共有グループから脱退するとメンバー数が0になる場合、共有グループは削除される")
    fun membersEmptyAfterLeaveSharedGroup_sharedGroupIsDeleted() {
        //given:
        val joinedSharedGroup = testSharedGroupInserter.insert(members = setOf(requesterAccountId),
                                                               invitees = setOf(userAccountIds[0]))
        val sharedGroupMedicine = testMedicineInserter.insert(MedicineOwner.create(joinedSharedGroup.id))

        //when:
        sharedGroupLeaveService.leaveAndCloneMedicines(requesterAccountId)

        //then:
        val foundSharedGroup = sharedGroupRepository.findById(joinedSharedGroup.id)
        assertThat(foundSharedGroup).isNull()
        val foundSharedGroupMedicine = medicineRepository.findById(sharedGroupMedicine.id)
        assertThat(foundSharedGroupMedicine).isNull()
    }
}