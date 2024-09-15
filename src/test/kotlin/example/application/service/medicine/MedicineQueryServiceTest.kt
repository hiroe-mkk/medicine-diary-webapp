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
class MedicineQueryServiceTest(@Autowired private val medicineRepository: MedicineRepository,
                               @Autowired private val medicineFinder: MedicineFinder,
                               @Autowired private val testAccountInserter: TestAccountInserter,
                               @Autowired private val testMedicineInserter: TestMedicineInserter) {
    private val medicineRegistrationService = MedicineQueryService(medicineRepository, medicineFinder)

    private lateinit var userSession: UserSession

    @BeforeEach
    internal fun setUp() {
        userSession = UserSessionFactory.create(testAccountInserter.insertAccountAndProfile().first.id)
    }

    @Nested
    inner class GetMedicineDetailTest {
        @Test
        @DisplayName("薬詳細を取得する")
        fun getMedicineDetail() {
            //given:
            val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))

            //when:
            val actual = medicineRegistrationService.findMedicine(medicine.id, userSession)

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
            val otherUserAccountId = testAccountInserter.insertAccountAndProfile().first.id
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(otherUserAccountId))

            //when:
            val target: () -> Unit = { medicineRegistrationService.findMedicine(medicine.id, userSession) }

            //then:
            val medicineNotFoundException = assertThrows<MedicineNotFoundException>(target)
            assertThat(medicineNotFoundException.medicineId).isEqualTo(medicine.id)
        }
    }

}
