package example.domain.model.sharedgroup

import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.medicine.*
import example.domain.model.medicine.medicineimage.*
import example.domain.model.medicationrecord.*
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
    private lateinit var user1AccountId: AccountId

    @BeforeEach
    internal fun setUp() {
        requesterAccountId = testAccountInserter.insertAccountAndProfile().first.id
        user1AccountId = testAccountInserter.insertAccountAndProfile().first.id
    }

    @Test
    @DisplayName("共有を解除する")
    fun unshareSharedGroup() {
        //given:
        val sharedGroup = testSharedGroupInserter.insert(members = setOf(requesterAccountId, user1AccountId))

        //when:
        sharedGroupUnshareService.unshare(requesterAccountId)

        //then:
        val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
        assertThat(foundSharedGroup?.members).containsExactlyInAnyOrder(user1AccountId)
        assertThat(foundSharedGroup?.invitees).isEmpty()
    }

    @Test
    @DisplayName("削除するとメンバー数が0になる場合、共有グループは削除される")
    fun memberIsEmptyAfterDeletion_SharedGroupIsDeleted() {
        //given:
        val sharedGroup = testSharedGroupInserter.insert(members = setOf(requesterAccountId))
        val sharedGroupMedicine = testMedicineInserter.insert(MedicineOwner.create(sharedGroup.id))
        val medicationRecord = testMedicationRecordInserter.insert(requesterAccountId, sharedGroupMedicine.id)

        //when:
        sharedGroupUnshareService.unshare(requesterAccountId)

        //then:
        val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
        assertThat(foundSharedGroup).isNull()
        val foundMedicines = medicineRepository.findByOwner(sharedGroup.id)
        assertThat(foundMedicines).isEmpty()
        val foundMedicationRecords = medicationRecordRepository.findById(medicationRecord.id)
        assertThat(foundMedicationRecords).isNull()
    }
}