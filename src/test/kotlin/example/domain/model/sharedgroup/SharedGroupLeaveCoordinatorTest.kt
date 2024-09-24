package example.domain.model.sharedgroup

import example.domain.model.account.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@DomainLayerTest
class SharedGroupLeaveCoordinatorTest(@Autowired private val sharedGroupRepository: SharedGroupRepository,
                                      @Autowired private val medicineRepository: MedicineRepository,
                                      @Autowired private val medicationRecordRepository: MedicationRecordRepository,
                                      @Autowired private val sharedGroupLeaveCoordinator: SharedGroupLeaveCoordinator,
                                      @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                      @Autowired private val testAccountInserter: TestAccountInserter,
                                      @Autowired private val testMedicineInserter: TestMedicineInserter,
                                      @Autowired private val testMedicationRecordInserter: TestMedicationRecordInserter) {
    private lateinit var requesterAccountId: AccountId
    private lateinit var otherUserAccountIds: List<AccountId>

    @BeforeEach
    internal fun setUp() {
        requesterAccountId = testAccountInserter.insertAccountAndProfile().first.id
        otherUserAccountIds = listOf(testAccountInserter.insertAccountAndProfile().first.id,
                                     testAccountInserter.insertAccountAndProfile().first.id)
    }

    @Test
    @DisplayName("共有グループから脱退する")
    fun leaveSharedGroup() {
        //given:
        val participatingSharedGroup =
                testSharedGroupInserter.insert(members = setOf(requesterAccountId, otherUserAccountIds[0]))

        //when:
        sharedGroupLeaveCoordinator.leaveSharedGroup(requesterAccountId)

        //then:
        val foundParticipatedSharedGroup = sharedGroupRepository.findById(participatingSharedGroup.id)
        assertThat(foundParticipatedSharedGroup?.members).containsExactlyInAnyOrder(otherUserAccountIds[0])
    }

    @Test
    @DisplayName("薬を複製し、共有グループから脱退する")
    fun leaveSharedGroupAndCloneMedicines() {
        //given:
        val joinedSharedGroup =
                testSharedGroupInserter.insert(members = setOf(requesterAccountId, otherUserAccountIds[0]))
        val sharedGroupMedicine = testMedicineInserter.insert(MedicineOwner.create(joinedSharedGroup.id))
        val medicationRecord = testMedicationRecordInserter.insert(requesterAccountId, sharedGroupMedicine.id)

        //when:
        sharedGroupLeaveCoordinator.leaveSharedGroupAndCloneMedicines(requesterAccountId)

        //then:
        val foundSharedGroup = sharedGroupRepository.findById(joinedSharedGroup.id)
        assertThat(foundSharedGroup?.members).containsExactly(otherUserAccountIds[0])
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
                                                               pendingInvitations = setOf(
                                                                       SharedGroupFactory.createPendingInvitation()))
        val sharedGroupMedicine = testMedicineInserter.insert(MedicineOwner.create(joinedSharedGroup.id))

        //when:
        sharedGroupLeaveCoordinator.leaveSharedGroupAndCloneMedicines(requesterAccountId)

        //then:
        val foundSharedGroup = sharedGroupRepository.findById(joinedSharedGroup.id)
        assertThat(foundSharedGroup).isNull()
        val foundSharedGroupMedicine = medicineRepository.findById(sharedGroupMedicine.id)
        assertThat(foundSharedGroupMedicine).isNull()
    }
}
