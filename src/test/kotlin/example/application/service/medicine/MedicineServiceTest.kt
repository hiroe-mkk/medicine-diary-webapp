package example.application.service.medicine

import example.application.shared.usersession.*
import example.domain.model.account.*
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
                                   @Autowired private val testMedicineInserter: TestMedicineInserter,
                                   @Autowired private val testSharedGroupInserter: TestSharedGroupInserter) {
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
        fun findMedicineOverviews() {
            //given:
            val memberUserAccountId = testAccountInserter.insertAccountAndProfile().first.id
            val sharedGroupId =
                    testSharedGroupInserter.insert(members = setOf(userSession.accountId, memberUserAccountId)).id
            val (ownedMedicine1, ownedMedicine2, ownedMedicine3) =
                    createMedicines(MedicineOwner.create(userSession.accountId))
            val (sharedGroupMedicine1, sharedGroupMedicine2, sharedGroupMedicine3) =
                    createMedicines(MedicineOwner.create(sharedGroupId))
            val (memberMedicine1, memberMedicine2, memberMedicine3) =
                    createMedicines(MedicineOwner.create(memberUserAccountId))

            //when:
            val actual = medicineService.findMedicineOverviews(userSession)

            //then:
            assertThat(actual.ownedMedicines).extracting("medicineId")
                .containsExactly(ownedMedicine3.id, ownedMedicine2.id, ownedMedicine1.id)
            assertThat(actual.sharedGroupMedicines).extracting("medicineId")
                .containsExactly(sharedGroupMedicine3.id, sharedGroupMedicine2.id, sharedGroupMedicine1.id)
            assertThat(actual.membersMedicines).extracting("medicineId")
                .containsExactly(memberMedicine3.id, memberMedicine2.id, memberMedicine1.id)
        }

        @Test
        @DisplayName("ユーザーの薬概要一覧を取得する")
        fun findUserMedicineOverviews() {
            //given:
            val (medicine1, medicine2, medicine3) = createMedicines(MedicineOwner.create(userSession.accountId))

            //when:
            val actual = medicineService.findUserMedicineOverviews(userSession)

            //then:
            assertThat(actual).extracting("medicineId").containsExactly(medicine3.id, medicine2.id, medicine1.id)
        }

        private fun createMedicines(medicineOwner: MedicineOwner): Triple<Medicine, Medicine, Medicine> {
            val localDateTime = LocalDateTime.of(2020, 1, 1, 0, 0)
            val medicine1 = testMedicineInserter.insert(owner = medicineOwner,
                                                        registeredAt = localDateTime,
                                                        isPublic = true)
            val medicine2 = testMedicineInserter.insert(owner = medicineOwner,
                                                        registeredAt = localDateTime.plusDays(1),
                                                        isPublic = true)
            val medicine3 = testMedicineInserter.insert(owner = medicineOwner,
                                                        registeredAt = localDateTime.plusDays(2),
                                                        isPublic = true)
            return Triple(medicine1, medicine2, medicine3)
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