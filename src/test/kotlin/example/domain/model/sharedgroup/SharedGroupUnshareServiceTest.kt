package example.domain.model.sharedgroup

import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.medicine.*
import example.domain.model.medicine.medicineImage.*
import example.domain.model.takingrecord.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import io.mockk.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@MyBatisRepositoryTest
internal class SharedGroupUnshareServiceTest(@Autowired private val sharedGroupRepository: SharedGroupRepository,
                                             @Autowired private val medicineRepository: MedicineRepository,
                                             @Autowired private val takingRecordRepository: TakingRecordRepository,
                                             @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                             @Autowired private val testAccountInserter: TestAccountInserter,
                                             @Autowired private val testMedicineInserter: TestMedicineInserter,
                                             @Autowired private val testTakingRecordInserter: TestTakingRecordInserter) {
    private val sharedGroupQueryService: SharedGroupQueryService = SharedGroupQueryService(sharedGroupRepository)
    private val medicineImageStorage: MedicineImageStorage = mockk(relaxed = true)
    private val medicineQueryService: MedicineQueryService =
            MedicineQueryService(medicineRepository, sharedGroupRepository)
    private val medicineAndTakingRecordsDeletionService: MedicineAndTakingRecordsDeletionService =
            MedicineAndTakingRecordsDeletionService(medicineRepository,
                                                    medicineImageStorage,
                                                    takingRecordRepository,
                                                    medicineQueryService)
    private val sharedGroupUnshareService: SharedGroupUnshareService = SharedGroupUnshareService(sharedGroupRepository,
                                                                                                 sharedGroupQueryService,
                                                                                                 medicineAndTakingRecordsDeletionService)

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
        sharedGroupUnshareService.unshare(sharedGroup.id, requesterAccountId)

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
        val takingRecord = testTakingRecordInserter.insert(requesterAccountId, sharedGroupMedicine.id)

        //when:
        sharedGroupUnshareService.unshare(sharedGroup.id, requesterAccountId)

        //then:
        val foundSharedGroup = sharedGroupRepository.findById(sharedGroup.id)
        assertThat(foundSharedGroup).isNull()
        val foundMedicines = medicineRepository.findByOwner(sharedGroup.id)
        assertThat(foundMedicines).isEmpty()
        val foundTakingRecords = takingRecordRepository.findById(takingRecord.id)
        assertThat(foundTakingRecords).isNull()
    }
}