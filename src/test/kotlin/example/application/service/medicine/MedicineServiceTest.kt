package example.application.service.medicine

import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.medicine.*
import example.domain.shared.type.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@DomainLayerTest
internal class MedicineServiceTest(@Autowired private val medicineRepository: MedicineRepository,
                                   @Autowired private val localDateTimeProvider: LocalDateTimeProvider,
                                   @Autowired private val medicineFinder: MedicineFinder,
                                   @Autowired private val medicineCreationService: MedicineCreationService,
                                   @Autowired private val medicineBasicInfoUpdateService: MedicineBasicInfoUpdateService,
                                   @Autowired private val medicineDeletionService: MedicineDeletionService,
                                   @Autowired private val testAccountInserter: TestAccountInserter,
                                   @Autowired private val testMedicineInserter: TestMedicineInserter) {
    private val medicineService: MedicineService = MedicineService(medicineRepository,
                                                                   localDateTimeProvider,
                                                                   medicineFinder,
                                                                   medicineCreationService,
                                                                   medicineBasicInfoUpdateService,
                                                                   medicineDeletionService)

    private lateinit var userSession: UserSession
    private lateinit var user1AccountId: AccountId

    @BeforeEach
    internal fun setUp() {
        val requesterAccountId = testAccountInserter.insertAccountAndProfile().first.id
        userSession = UserSessionFactory.create(requesterAccountId)
        user1AccountId = testAccountInserter.insertAccountAndProfile().first.id
    }

    @Nested
    inner class GetMedicineDetailTest {
        @Test
        @DisplayName("薬詳細を取得する")
        fun getMedicineDetail() {
            //given:
            val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))

            //when:
            val actual = medicineService.findMedicine(medicine.id, userSession)

            //then:
            val expected = MedicineDto(medicine.id,
                                       medicine.owner,
                                       medicine.medicineName,
                                       medicine.dosageAndAdministration,
                                       medicine.effects,
                                       medicine.precautions,
                                       medicine.medicineImageURL,
                                       medicine.isPublic,
                                       medicine.inventory,
                                       medicine.registeredAt)
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        @DisplayName("閲覧可能な薬が見つからなかった場合、薬詳細の取得に失敗する")
        fun viewableMedicineNotFound_gettingMedicineDetailFails() {
            //given:
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(user1AccountId))

            //when:
            val target: () -> Unit = { medicineService.findMedicine(medicine.id, userSession) }

            //then:
            val medicineNotFoundException = assertThrows<MedicineNotFoundException>(target)
            assertThat(medicineNotFoundException.medicineId).isEqualTo(medicine.id)
        }
    }

    @Nested
    inner class RegisterMedicineTest {
        @Test
        @DisplayName("薬を登録する")
        fun registerMedicine() {
            //given:
            val command = TestMedicineFactory.createCompletedRegistrationMedicineBasicInfoEditCommand()

            //when:
            val medicineId = medicineService.registerMedicine(command, userSession)

            //then:
            val foundMedicine = medicineRepository.findById(medicineId)
            val expected = Medicine(medicineId,
                                    MedicineOwner.create(userSession.accountId),
                                    command.validatedMedicineName,
                                    command.validatedDosageAndAdministration,
                                    command.validatedEffects,
                                    command.validatedPrecautions,
                                    null,
                                    command.isPublic,
                                    null,
                                    localDateTimeProvider.now())
            assertThat(foundMedicine).usingRecursiveComparison().isEqualTo(expected)
        }
    }

    @Nested
    inner class AdjustInventoryTest {
        private val command = TestMedicineFactory.createCompletedInventoryAdjustmentCommand()

        @Test
        @DisplayName("在庫を修正する")
        fun adjustInventory() {
            //given:
            val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))

            //when:
            medicineService.adjustInventory(medicine.id, command, userSession)

            //then:
            val foundMedicine = medicineRepository.findById(medicine.id)
            assertThat(foundMedicine?.inventory).isEqualTo(command.validatedInventory)
        }

        @Test
        @DisplayName("服用可能な薬が見つからなかった場合、在庫の修正に失敗する")
        fun availableMedicineNotFound_adjustingInventoryFails() {
            //given:
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(user1AccountId))

            //when:
            val target: () -> Unit = {
                medicineService.adjustInventory(medicine.id, command, userSession)
            }

            //then:
            val medicineNotFoundException = assertThrows<MedicineNotFoundException>(target)
            assertThat(medicineNotFoundException.medicineId).isEqualTo(medicine.id)
        }
    }

    @Nested
    inner class StopInventoryManagementTest {
        @Test
        @DisplayName("在庫管理を終了する")
        fun stopInventoryManagement() {
            //given:
            val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))

            //when:
            medicineService.stopInventoryManagement(medicine.id, userSession)

            //then:
            val foundMedicine = medicineRepository.findById(medicine.id)
            assertThat(foundMedicine?.inventory).isNull()
        }
    }
}
