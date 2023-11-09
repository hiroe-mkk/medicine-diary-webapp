package example.application.service.medicine

import example.application.shared.usersession.*
import example.domain.model.medicine.*
import example.domain.model.medicine.medicineImage.*
import example.domain.model.sharedgroup.*
import example.domain.shared.type.*
import example.infrastructure.storage.medicineimage.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import io.mockk.*
import io.mockk.impl.annotations.*
import org.assertj.core.api.Assertions.*
import org.checkerframework.checker.units.qual.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import java.time.*

@MyBatisRepositoryTest
internal class MedicineServiceTest(@Autowired private val medicineRepository: MedicineRepository,
                                   @Autowired private val sharedGroupRepository: SharedGroupRepository,
                                   @Autowired private val testAccountInserter: TestAccountInserter,
                                   @Autowired private val testMedicineInserter: TestMedicineInserter) {
    private val localDateTimeProvider: LocalDateTimeProvider = mockk()
    private val medicineDomainService: MedicineDomainService =
            MedicineDomainService(medicineRepository, sharedGroupRepository)
    private val medicineService: MedicineService =
            MedicineService(medicineRepository, localDateTimeProvider, medicineDomainService)

    private lateinit var userSession: UserSession

    @BeforeEach
    internal fun setUp() {
        val (account, _) = testAccountInserter.insertAccountAndProfile()
        userSession = UserSessionFactory.create(account.id)
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
                                       medicine.registeredAt)
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        @DisplayName("薬が見つからなかった場合、薬詳細の取得に失敗する")
        fun medicineNotFound_gettingMedicineDetailFails() {
            //given:
            val badMedicineId = MedicineId("NonexistentId")

            //when:
            val target: () -> Unit = { medicineService.findMedicine(badMedicineId, userSession) }

            //then:
            val medicineNotFoundException = assertThrows<MedicineNotFoundException>(target)
            assertThat(medicineNotFoundException.medicineId).isEqualTo(badMedicineId)
        }
    }

    @Nested
    inner class GetMedicineOverviewsTest {
        @Test
        @DisplayName("薬概要一覧を取得する")
        fun findAllMedicineOverviews() {
            //given:
            val localDateTime = LocalDateTime.of(2020, 1, 1, 0, 0)
            val medicine1 = testMedicineInserter.insert(owner = MedicineOwner.create(userSession.accountId),
                                                        registeredAt = localDateTime)
            val medicine2 = testMedicineInserter.insert(owner = MedicineOwner.create(userSession.accountId),
                                                        registeredAt = localDateTime.plusDays(1))
            val medicine3 = testMedicineInserter.insert(owner = MedicineOwner.create(userSession.accountId),
                                                        registeredAt = localDateTime.plusDays(2))

            //when:
            val actual = medicineService.findAllMedicineOverviews(userSession)

            //then:
            val expected = arrayOf(MedicineOverviewDto(medicine3.id,
                                                       medicine3.medicineName,
                                                       medicine3.dosageAndAdministration,
                                                       medicine3.medicineImageURL,
                                                       medicine3.effects),
                                   MedicineOverviewDto(medicine2.id,
                                                       medicine2.medicineName,
                                                       medicine2.dosageAndAdministration,
                                                       medicine2.medicineImageURL,
                                                       medicine2.effects),
                                   MedicineOverviewDto(medicine1.id,
                                                       medicine1.medicineName,
                                                       medicine1.dosageAndAdministration,
                                                       medicine1.medicineImageURL,
                                                       medicine1.effects))
            assertThat(actual).containsExactly(*expected)
        }

        @Test
        @DisplayName("ユーザーの薬概要一覧を取得する")
        fun findUserMedicineOverviews() {
            //given:
            val localDateTime = LocalDateTime.of(2020, 1, 1, 0, 0)
            val medicine1 = testMedicineInserter.insert(owner = MedicineOwner.create(userSession.accountId),
                                                        registeredAt = localDateTime)
            val medicine2 = testMedicineInserter.insert(owner = MedicineOwner.create(userSession.accountId),
                                                        registeredAt = localDateTime.plusDays(1))
            val medicine3 = testMedicineInserter.insert(owner = MedicineOwner.create(userSession.accountId),
                                                        registeredAt = localDateTime.plusDays(2))

            //when:
            val actual = medicineService.findUserMedicineOverviews(userSession)

            //then:
            val expected = arrayOf(MedicineOverviewDto(medicine3.id,
                                                       medicine3.medicineName,
                                                       medicine3.dosageAndAdministration,
                                                       medicine3.medicineImageURL,
                                                       medicine3.effects),
                                   MedicineOverviewDto(medicine2.id,
                                                       medicine2.medicineName,
                                                       medicine2.dosageAndAdministration,
                                                       medicine2.medicineImageURL,
                                                       medicine2.effects),
                                   MedicineOverviewDto(medicine1.id,
                                                       medicine1.medicineName,
                                                       medicine1.dosageAndAdministration,
                                                       medicine1.medicineImageURL,
                                                       medicine1.effects))
            assertThat(actual).containsExactly(*expected)
        }
    }

    @Nested
    inner class RegisterMedicineTest {
        @Test
        @DisplayName("薬を登録する")
        fun registerMedicine() {
            //given:
            val command = TestMedicineFactory.createCompletedRegistrationCommand()
            val localDateTime = LocalDateTime.of(2020, 1, 1, 0, 0)
            every { localDateTimeProvider.now() } returns localDateTime

            //when:
            val medicineId = medicineService.registerMedicine(command, true, userSession)

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
                                    localDateTime)
            assertThat(foundMedicine).usingRecursiveComparison().isEqualTo(expected)
        }
    }

    @Nested
    inner class UpdateMedicineBasicInfoTest {
        @Test
        @DisplayName("薬基本情報を更新する")
        fun updateMedicineBasicInfo() {
            //given:
            val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))
            val command = TestMedicineFactory.createCompletedUpdateCommand()

            //when:
            medicineService.updateMedicineBasicInfo(medicine.id, command, userSession)

            //then:
            val foundMedicine = medicineRepository.findById(medicine.id)
            val expected = Medicine(medicine.id,
                                    medicine.owner,
                                    command.validatedMedicineName,
                                    command.validatedDosageAndAdministration,
                                    command.validatedEffects,
                                    command.validatedPrecautions,
                                    medicine.medicineImageURL,
                                    command.isPublic,
                                    medicine.registeredAt)
            assertThat(foundMedicine).usingRecursiveComparison().isEqualTo(expected)
        }

        @Test
        @DisplayName("薬が見つからなかった場合、薬基本情報の更新に失敗する")
        fun medicineNotFound_updatingMedicineBasicInfoFails() {
            //given:
            val badMedicineId = MedicineId("NonexistentId")
            val command = TestMedicineFactory.createCompletedRegistrationCommand()

            //when:
            val target: () -> Unit = { medicineService.updateMedicineBasicInfo(badMedicineId, command, userSession) }

            //then:
            val medicineNotFoundException = assertThrows<MedicineNotFoundException>(target)
            assertThat(medicineNotFoundException.medicineId).isEqualTo(badMedicineId)
        }
    }

    @Nested
    inner class DeleteMedicineTest {
        @Test
        @DisplayName("薬を削除する")
        fun deleteMedicine() {
            //given:
            val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))

            //when:
            medicineService.deleteMedicine(medicine.id, userSession)

            //then:
            val foundMedicine = medicineRepository.findById(medicine.id)
            assertThat(foundMedicine).isNull()
        }

        @Test
        @DisplayName("薬が見つからなかった場合、薬の削除に失敗する")
        fun medicineNotFound_deletingMedicineFails() {
            //given:
            val badMedicineId = MedicineId("NonexistentId")

            //when:
            val target: () -> Unit = { medicineService.deleteMedicine(badMedicineId, userSession) }

            //then:
            val medicineNotFoundException = assertThrows<MedicineNotFoundException>(target)
            assertThat(medicineNotFoundException.medicineId).isEqualTo(badMedicineId)
        }
    }
}