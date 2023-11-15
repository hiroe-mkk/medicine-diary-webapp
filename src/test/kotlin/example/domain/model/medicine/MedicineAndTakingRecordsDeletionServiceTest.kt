package example.domain.model.medicine

import example.application.service.medicine.*
import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.medicine.medicineImage.*
import example.domain.model.sharedgroup.*
import example.domain.model.takingrecord.*
import example.domain.shared.type.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import io.mockk.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@MyBatisRepositoryTest
internal class MedicineAndTakingRecordsDeletionServiceTest(@Autowired private val medicineRepository: MedicineRepository,
                                                           @Autowired private val takingRecordRepository: TakingRecordRepository,
                                                           @Autowired private val sharedGroupRepository: SharedGroupRepository,
                                                           @Autowired private val testAccountInserter: TestAccountInserter,
                                                           @Autowired private val testSharedGroupInserter: TestSharedGroupInserter,
                                                           @Autowired private val testMedicineInserter: TestMedicineInserter,
                                                           @Autowired private val testTakingRecordInserter: TestTakingRecordInserter) {
    private val medicineImageStorage: MedicineImageStorage = mockk(relaxed = true)
    private val medicineQueryService: MedicineQueryService =
            MedicineQueryService(medicineRepository, sharedGroupRepository)
    private val medicineAndTakingRecordsDeletionService: MedicineAndTakingRecordsDeletionService =
            MedicineAndTakingRecordsDeletionService(medicineRepository,
                                                    medicineImageStorage,
                                                    takingRecordRepository,
                                                    medicineQueryService)
    private lateinit var requesterAccountId: AccountId

    @BeforeEach
    internal fun setUp() {
        requesterAccountId = testAccountInserter.insertAccountAndProfile().first.id
    }

    @Test
    @DisplayName("薬を削除する")
    fun deleteMedicine() {
        //given:
        val medicineImageURL = MedicineImageURL("endpoint", "/medicineimage/oldMedicineImage")
        val medicine = testMedicineInserter.insert(MedicineOwner.create(requesterAccountId),
                                                   medicineImageURL = medicineImageURL)
        val takingRecord = testTakingRecordInserter.insert(requesterAccountId, medicine.id)

        //when:
        medicineAndTakingRecordsDeletionService.delete(medicine.id, requesterAccountId)

        //then:
        val foundMedicine = medicineRepository.findById(medicine.id)
        assertThat(foundMedicine).isNull()
        val foundTakingRecord = takingRecordRepository.findById(takingRecord.id)
        assertThat(foundTakingRecord).isNull()
        verify(exactly = 1) { medicineImageStorage.delete(medicineImageURL) }
    }

    @Test
    @DisplayName("共有グループの薬をすべて削除する")
    fun deleteAllSharedGroupMedicines() {
        //given
        val sharedGroup = testSharedGroupInserter.insert(members = setOf(requesterAccountId))
        val takingRecordIds = List(3) {
            val medicine = testMedicineInserter.insert(MedicineOwner.create(sharedGroup.id))
            testTakingRecordInserter.insert(requesterAccountId, medicine.id).id
        }

        //when:
        medicineAndTakingRecordsDeletionService.deleteAllSharedGroupMedicines(sharedGroup.id)

        //then:
        val foundMedicines = medicineRepository.findByOwner(sharedGroup.id)
        assertThat(foundMedicines).isEmpty()
        val foundTakingRecords = takingRecordIds.mapNotNull { takingRecordRepository.findById(it) }
        assertThat(foundTakingRecords).isEmpty()
    }
}