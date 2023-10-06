package example.application.service.medicine

import example.application.shared.usersession.*
import example.domain.model.medicine.*
import example.domain.shared.type.*
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
    inner class GetMedicineTest {
        @Test
        @DisplayName("薬を取得する")
        fun getMedicine() {
            //given:
            val medicine = testMedicineInserter.insert(userSession.accountId)

            //when:
            val actual = medicineService.findMedicine(medicine.id, userSession)

            //then:
            val expected = MedicineDto(medicine.id,
                                       medicine.name,
                                       medicine.takingUnit,
                                       medicine.dosage,
                                       medicine.administration,
                                       medicine.effects,
                                       medicine.precautions,
                                       medicine.registeredAt)
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        @DisplayName("薬が見つからなかった場合、薬の取得に失敗する")
        fun medicineNotFound_gettingMedicineFails() {
            //given:
            val badMedicineId = MedicineId("NonexistentId")

            //when:
            val target: () -> Unit = { medicineService.findMedicine(badMedicineId, userSession) }

            //then:
            val medicineNotFoundException = assertThrows<MedicineNotFoundException>(target)
            assertThat(medicineNotFoundException.medicineId).isEqualTo(badMedicineId)
        }

        @Test
        @DisplayName("ユーザーが所有していない薬の場合、薬の取得に失敗する")
        fun medicineIsNotOwnedByUser_gettingMedicineFails() {
            //given:
            val (anotherAccount, _) = testAccountInserter.insertAccountAndProfile()
            val medicine = testMedicineInserter.insert(anotherAccount.id)

            //when:
            val target: () -> Unit = { medicineService.findMedicine(medicine.id, userSession) }

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
                                                       medicine3.takingUnit,
                                                       medicine3.dosage,
                                                       medicine3.administration,
                                                       medicine3.effects),
                                   MedicineOverviewDto(medicine2.id,
                                                       medicine2.name,
                                                       medicine2.takingUnit,
                                                       medicine2.dosage,
                                                       medicine2.administration,
                                                       medicine2.effects),
                                   MedicineOverviewDto(medicine1.id,
                                                       medicine1.name,
                                                       medicine1.takingUnit,
                                                       medicine1.dosage,
                                                       medicine1.administration,
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
                                    command.validatedTakingUnit,
                                    command.validatedDosage,
                                    command.validatedAdministration,
                                    command.validatedEffects,
                                    command.validatedPrecautions,
                                    localDateTime)
            assertThat(foundMedicine).usingRecursiveComparison().isEqualTo(expected)
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