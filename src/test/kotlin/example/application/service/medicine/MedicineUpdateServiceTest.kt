package example.application.service.medicine

import example.application.shared.usersession.*
import example.domain.model.medicine.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@DomainLayerTest
class MedicineUpdateServiceTest(@Autowired private val medicineRepository: MedicineRepository,
                                @Autowired private val medicineFinder: MedicineFinder,
                                @Autowired private val medicineBasicInfoUpdater: MedicineBasicInfoUpdater,
                                @Autowired private val testAccountInserter: TestAccountInserter,
                                @Autowired private val testMedicineInserter: TestMedicineInserter) {
    private val medicineRegistrationService = MedicineUpdateService(medicineRepository,
                                                                    medicineFinder,
                                                                    medicineBasicInfoUpdater)

    private lateinit var userSession: UserSession

    @BeforeEach
    internal fun setUp() {
        userSession = UserSessionFactory.create(testAccountInserter.insertAccountAndProfile().first.id)
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
            medicineRegistrationService.adjustInventory(medicine.id, command, userSession)

            //then:
            val foundMedicine = medicineRepository.findById(medicine.id)
            assertThat(foundMedicine?.inventory).isEqualTo(command.validatedInventory)
        }

        @Test
        @DisplayName("服用可能な薬が見つからなかった場合、在庫の修正に失敗する")
        fun availableMedicineNotFound_adjustingInventoryFails() {
            //given:
            val otherUserAccountId = testAccountInserter.insertAccountAndProfile().first.id
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(otherUserAccountId))

            //when:
            val target: () -> Unit = {
                medicineRegistrationService.adjustInventory(medicine.id, command, userSession)
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
            medicineRegistrationService.stopInventoryManagement(medicine.id, userSession)

            //then:
            val foundMedicine = medicineRepository.findById(medicine.id)
            assertThat(foundMedicine?.inventory).isNull()
        }
    }
}
