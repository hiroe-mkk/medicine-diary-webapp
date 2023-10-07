package example.application.service.medicine

import example.application.shared.usersession.*
import example.domain.model.medicine.*
import example.domain.model.medicine.medicineImage.*
import example.domain.shared.type.*
import example.infrastructure.storage.medicineimage.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import io.mockk.*
import io.mockk.impl.annotations.*
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import java.time.*

@MyBatisRepositoryTest
internal class MedicineServiceTest(@Autowired private val medicineRepository: MedicineRepository,
                                   @Autowired private val testAccountInserter: TestAccountInserter,
                                   @Autowired private val testMedicineInserter: TestMedicineInserter) {
    @MockK(relaxed = true)
    private lateinit var medicineImageStorage: MedicineImageStorage

    @MockK
    private lateinit var localDateTimeProvider: LocalDateTimeProvider

    @InjectMockKs
    private lateinit var medicineService: MedicineService

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
            val medicine = testMedicineInserter.insert(userSession.accountId)

            //when:
            val actual = medicineService.findMedicineDetail(medicine.id, userSession)

            //then:
            val expected = MedicineDetailDto(medicine.id,
                                             medicine.name,
                                             medicine.dosage,
                                             medicine.administration,
                                             medicine.effects,
                                             medicine.precautions,
                                             medicine.medicineImageURL,
                                             medicine.registeredAt)
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        @DisplayName("薬が見つからなかった場合、薬詳細の取得に失敗する")
        fun medicineNotFound_gettingMedicineDetailFails() {
            //given:
            val badMedicineId = MedicineId("NonexistentId")

            //when:
            val target: () -> Unit = { medicineService.findMedicineDetail(badMedicineId, userSession) }

            //then:
            val medicineNotFoundException = assertThrows<MedicineNotFoundException>(target)
            assertThat(medicineNotFoundException.medicineId).isEqualTo(badMedicineId)
        }

        @Test
        @DisplayName("ユーザーが所有していない薬の場合、薬詳細の取得に失敗する")
        fun medicineIsNotOwnedByUser_gettingMedicineDetailFails() {
            //given:
            val (anotherAccount, _) = testAccountInserter.insertAccountAndProfile()
            val medicine = testMedicineInserter.insert(anotherAccount.id)

            //when:
            val target: () -> Unit = { medicineService.findMedicineDetail(medicine.id, userSession) }

            //then:
            val medicineNotFoundException = assertThrows<MedicineNotFoundException>(target)
            assertThat(medicineNotFoundException.medicineId).isEqualTo(medicine.id)
        }
    }

    @Nested
    inner class GetMedicineOverviewsTest {
        @Test
        @DisplayName("薬概要一覧を取得する")
        fun findAllMedicineOverviews() {
            //given:
            val localDateTime = LocalDateTime.of(2020, 1, 1, 0, 0)
            val medicine1 = testMedicineInserter.insert(owner = userSession.accountId, registeredAt = localDateTime)
            val medicine2 = testMedicineInserter.insert(owner = userSession.accountId,
                                                        registeredAt = localDateTime.plusDays(1))
            val medicine3 = testMedicineInserter.insert(owner = userSession.accountId,
                                                        registeredAt = localDateTime.plusDays(2))

            //when:
            val actual = medicineService.findAllMedicineOverviews(userSession)

            //then:
            val expected = arrayOf(MedicineOverviewDto(medicine3.id,
                                                       medicine3.name,
                                                       medicine3.dosage,
                                                       medicine3.administration,
                                                       medicine3.medicineImageURL,
                                                       medicine3.effects),
                                   MedicineOverviewDto(medicine2.id,
                                                       medicine2.name,
                                                       medicine2.dosage,
                                                       medicine2.administration,
                                                       medicine2.medicineImageURL,
                                                       medicine2.effects),
                                   MedicineOverviewDto(medicine1.id,
                                                       medicine1.name,
                                                       medicine1.dosage,
                                                       medicine1.administration,
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
            val command = TestMedicineFactory.createMedicineBasicInfoInputCommand()
            val localDateTime = LocalDateTime.of(2020, 1, 1, 0, 0)
            every { localDateTimeProvider.now() } returns localDateTime

            //when:
            val medicineId = medicineService.registerMedicine(command, userSession)

            //then:
            val foundMedicine = medicineRepository.findById(medicineId)
            val expected = Medicine(medicineId,
                                    userSession.accountId,
                                    command.validatedName,
                                    command.validatedDosage,
                                    command.validatedAdministration,
                                    command.validatedEffects,
                                    command.validatedPrecautions,
                                    null,
                                    localDateTime)
            assertThat(foundMedicine).usingRecursiveComparison().isEqualTo(expected)
        }
    }

    @Nested
    inner class UpdateMedicineBasicInfoTest {
        @Test
        @DisplayName("薬の基本情報を更新する")
        fun updateMedicineBasicInfo() {
            //given:
            val medicine = testMedicineInserter.insert(userSession.accountId)
            val command = TestMedicineFactory.createMedicineBasicInfoInputCommand(medicine)

            //when:
            medicineService.updateMedicineBasicInfo(medicine.id, command, userSession)

            //then:
            val foundMedicine = medicineRepository.findById(medicine.id)
            val expected = Medicine(medicine.id,
                                    medicine.owner,
                                    command.validatedName,
                                    command.validatedDosage,
                                    command.validatedAdministration,
                                    command.validatedEffects,
                                    command.validatedPrecautions,
                                    medicine.medicineImageURL,
                                    medicine.registeredAt)
            assertThat(foundMedicine).usingRecursiveComparison().isEqualTo(expected)
        }

        @Test
        @DisplayName("薬が見つからなかった場合、薬の基本情報の更新に失敗する")
        fun medicineNotFound_updatingMedicineBasicInfoFails() {
            //given:
            val badMedicineId = MedicineId("nonexistentId")
            val command = TestMedicineFactory.createMedicineBasicInfoInputCommand()

            //when:
            val target: () -> Unit = { medicineService.updateMedicineBasicInfo(badMedicineId, command, userSession) }

            //then:
            val medicineNotFoundException = assertThrows<MedicineNotFoundException>(target)
            assertThat(medicineNotFoundException.medicineId).isEqualTo(badMedicineId)
        }

        @Test
        @DisplayName("ユーザーが所有していない薬の場合、薬の基本情報の更新に失敗する")
        fun medicineIsNotOwnedByUser_updatingMedicineBasicInfoFails() {
            //given:
            val (anotherAccount, _) = testAccountInserter.insertAccountAndProfile()
            val medicine = testMedicineInserter.insert(anotherAccount.id)
            val command = TestMedicineFactory.createMedicineBasicInfoInputCommand(medicine)

            //when:
            val target: () -> Unit = { medicineService.updateMedicineBasicInfo(medicine.id, command, userSession) }

            //then:
            val medicineNotFoundException = assertThrows<MedicineNotFoundException>(target)
            assertThat(medicineNotFoundException.medicineId).isEqualTo(medicine.id)
        }
    }

    @Nested
    inner class ChangeMedicineImageTest {
        private lateinit var medicine: Medicine
        private val medicineImageChangeCommand = TestMedicineImageFactory.createMedicineImageChangeCommand()

        @BeforeEach
        internal fun setUp() {
            medicine = testMedicineInserter.insert(userSession.accountId)
            every {
                medicineImageStorage.createURL()
            } returns MedicineImageURL("endpoint", "/medicineimage/newMedicineImage")
        }

        @Test
        @DisplayName("薬画像が未設定の場合、薬画像の変更に成功する")
        fun medicineImageIsNull_changingMedicineImageSucceeds() {
            //when:
            val newMedicineImageURL = medicineService.changeMedicineImage(medicine.id,
                                                                          medicineImageChangeCommand,
                                                                          userSession)

            //then:
            val foundMedicine = medicineRepository.findById(medicine.id)!!
            assertThat(foundMedicine.medicineImageURL).isEqualTo(newMedicineImageURL)
            verify(exactly = 0) { medicineImageStorage.delete(any()) }
            verify(exactly = 1) { medicineImageStorage.upload(any()) }
        }

        @Test
        @DisplayName("薬画像が設定済みの場合、薬画像の変更に成功する")
        fun medicineImageIsNotNull_changingMedicineImageSucceeds() {
            //given:
            val oldMedicineImageURL = MedicineImageURL("endpoint", "/medicineimage/oldMedicineImage")
            medicine.changeMedicineImage(oldMedicineImageURL)
            medicineRepository.save(medicine)

            //when:
            val newMedicineImageURL = medicineService.changeMedicineImage(medicine.id,
                                                                          medicineImageChangeCommand,
                                                                          userSession)

            //then:
            val foundMedicine = medicineRepository.findById(medicine.id)!!
            assertThat(foundMedicine.medicineImageURL).isEqualTo(newMedicineImageURL)
            verify(exactly = 1) { medicineImageStorage.upload(any()) }
            verify(exactly = 1) { medicineImageStorage.delete(oldMedicineImageURL) }
        }

        @Test
        @DisplayName("薬が見つからなかった場合、薬画像の変更に失敗する")
        fun medicineNotFound_changingMedicineImageFails() {
            //given:
            val badMedicineId = MedicineId("NonexistentId")

            //when:
            val target: () -> Unit = {
                medicineService.changeMedicineImage(badMedicineId,
                                                    medicineImageChangeCommand,
                                                    userSession)
            }

            //then:
            val medicineNotFoundException = assertThrows<MedicineNotFoundException>(target)
            assertThat(medicineNotFoundException.medicineId).isEqualTo(badMedicineId)
        }

        @Test
        @DisplayName("ユーザーが所有していない薬の場合、薬画像の変更に失敗する")
        fun medicineIsNotOwnedByUser_changingMedicineImageFails() {
            //given:
            val (anotherAccount, _) = testAccountInserter.insertAccountAndProfile()
            val medicine = testMedicineInserter.insert(anotherAccount.id)

            //when:
            val target: () -> Unit = {
                medicineService.changeMedicineImage(medicine.id,
                                                    medicineImageChangeCommand,
                                                    userSession)
            }

            //then:
            val medicineNotFoundException = assertThrows<MedicineNotFoundException>(target)
            assertThat(medicineNotFoundException.medicineId).isEqualTo(medicine.id)
        }
    }

    @Nested
    inner class DeleteMedicineTest {
        @Test
        @DisplayName("薬を削除する")
        fun deleteMedicine() {
            //given:
            val medicine = testMedicineInserter.insert(userSession.accountId)

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

        @Test
        @DisplayName("ユーザーが所有していない薬の場合、薬の削除に失敗する")
        fun medicineIsNotOwnedByUser_deletingMedicineFails() {
            //given:
            val (anotherAccount, _) = testAccountInserter.insertAccountAndProfile()
            val medicine = testMedicineInserter.insert(anotherAccount.id)

            //when:
            val target: () -> Unit = { medicineService.deleteMedicine(medicine.id, userSession) }

            //then:
            val medicineNotFoundException = assertThrows<MedicineNotFoundException>(target)
            assertThat(medicineNotFoundException.medicineId).isEqualTo(medicine.id)
        }
    }
}