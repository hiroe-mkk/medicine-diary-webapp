package example.domain.model.medicine

import example.application.service.medicine.*
import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.sharedgroup.*
import example.domain.shared.type.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.junit.jupiter.params.*
import org.junit.jupiter.params.provider.*
import org.springframework.beans.factory.annotation.*
import java.time.*

@MyBatisRepositoryTest
internal class MedicineDomainServiceTest(@Autowired private val medicineRepository: MedicineRepository,
                                         @Autowired private val sharedGroupRepository: SharedGroupRepository,
                                         @Autowired private val testMedicineInserter: TestMedicineInserter,
                                         @Autowired private val testAccountInserter: TestAccountInserter,
                                         @Autowired private val testSharedGroupInserter: TestSharedGroupInserter) {
    private val medicineDomainService: MedicineDomainService =
            MedicineDomainService(medicineRepository, sharedGroupRepository)

    private lateinit var accountId: AccountId

    @BeforeEach
    internal fun setUp() {
        accountId = testAccountInserter.insertAccountAndProfile().first.id
    }

    @Nested
    inner class GetOwnedMedicineTest {
        @Test
        @DisplayName("所有している薬を取得する")
        fun getOwnedMedicine() {
            //given:
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(accountId))

            //when:
            val actual = medicineDomainService.findOwnedMedicine(medicine.id, accountId)

            //then:
            assertThat(actual).usingRecursiveComparison().isEqualTo(medicine)
        }

        @Test
        @DisplayName("自分以外のユーザーが所有する薬場合、薬の取得に失敗する")
        fun medicineOwnedByAnotherUser_gettingMedicineFails() {
            //given:
            val anotherAccountId = testAccountInserter.insertAccountAndProfile().first.id
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(anotherAccountId))

            //when:
            val actual = medicineDomainService.findOwnedMedicine(medicine.id, accountId)

            //then:
            assertThat(actual).isNull()
        }

        @Test
        @DisplayName("薬が見つからなかった場合、薬の取得に失敗する")
        fun medicineNotFound_gettingMedicineFails() {
            //given:
            val badMedicineId = MedicineId("NonexistentId")

            //when:
            val actual = medicineDomainService.findOwnedMedicine(badMedicineId, accountId)

            //then:
            assertThat(actual).isNull()
        }
    }

    @Nested
    inner class GetUserMedicineTest {
        @Test
        @DisplayName("所有している薬を取得する")
        fun getOwnedMedicine() {
            //given:
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(accountId))

            //when:
            val actual = medicineDomainService.findUserMedicine(medicine.id, accountId)

            //then:
            assertThat(actual).usingRecursiveComparison().isEqualTo(medicine)
        }

        @Test
        @DisplayName("自分以外のユーザーが所有する薬場合、薬の取得に失敗する")
        fun medicineOwnedByAnotherUser_gettingMedicineFails() {
            //given:
            val anotherAccountId = testAccountInserter.insertAccountAndProfile().first.id
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(anotherAccountId))

            //when:
            val actual = medicineDomainService.findUserMedicine(medicine.id, accountId)

            //then:
            assertThat(actual).isNull()
        }


        @Test
        @DisplayName("共有グループの薬を取得する")
        fun getSharedGroupMedicine() {
            //given:
            val sharedGroupId = testSharedGroupInserter.insert(members = setOf(accountId)).id
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(sharedGroupId))

            //when:
            val actual = medicineDomainService.findUserMedicine(medicine.id, accountId)

            //then:
            assertThat(actual).usingRecursiveComparison().isEqualTo(medicine)
        }

        @Test
        @DisplayName("参加していない共有グループが所有する薬場合、薬の取得に失敗する")
        fun medicineOwnedByNonParticipatingSharedGroup_gettingMedicineFails() {
            //given:
            val sharedGroupId = testSharedGroupInserter.insert().id
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(sharedGroupId))

            //when:
            val actual = medicineDomainService.findUserMedicine(medicine.id, accountId)

            //then:
            assertThat(actual).isNull()
        }

        @Test
        @DisplayName("薬が見つからなかった場合、薬の取得に失敗する")
        fun medicineNotFound_gettingMedicineFails() {
            //given:
            val badMedicineId = MedicineId("NonexistentId")

            //when:
            val actual = medicineDomainService.findUserMedicine(badMedicineId, accountId)

            //then:
            assertThat(actual).isNull()
        }
    }

    @Nested
    inner class CreateMedicineTest {
        private val medicineId = MedicineId("medicineId")
        private val medicineName: MedicineName = MedicineName("ロキソニンS")
        private val dosageAndAdministration: DosageAndAdministration = DosageAndAdministration(Dose(1.0),
                                                                                               "錠",
                                                                                               3,
                                                                                               emptyList())
        private val effects: Effects = Effects(listOf("頭痛", "解熱"))
        private val precautions: Note = Note("服用間隔は4時間以上開けること。")
        private val registeredAt = LocalDateTime.of(2020, 1, 1, 0, 0)

        @ParameterizedTest
        @CsvSource("true", "false")
        @DisplayName("所有する薬を作成する")
        fun createOwnedMedicine(isPublic: Boolean) {
            //given:
            val isWantToOwn = true

            //when:
            val actual = medicineDomainService.createMedicine(medicineId,
                                                              medicineName,
                                                              dosageAndAdministration,
                                                              effects,
                                                              precautions,
                                                              isPublic,
                                                              registeredAt,
                                                              accountId,
                                                              isWantToOwn)

            //then:
            assertThat(actual.owner).isEqualTo(MedicineOwner.create(accountId))
            assertThat(actual.isPublic).isEqualTo(isPublic)
        }

        @ParameterizedTest
        @CsvSource("true", "false")
        @DisplayName("共有グループの薬を作成する")
        fun createSharedGroupMedicine(isPublic: Boolean) {
            //given:
            val sharedGroupId = testSharedGroupInserter.insert(members = setOf(accountId)).id
            val isWantToOwn = false

            //when:
            val actual = medicineDomainService.createMedicine(medicineId,
                                                              medicineName,
                                                              dosageAndAdministration,
                                                              effects,
                                                              precautions,
                                                              isPublic,
                                                              registeredAt,
                                                              accountId,
                                                              isWantToOwn)

            //then:
            assertThat(actual.owner).isEqualTo(MedicineOwner.create(sharedGroupId))
            assertThat(actual.isPublic).isTrue
        }

        @ParameterizedTest
        @CsvSource("true", "false")
        @DisplayName("共有グループが見つからなかった場合、所有する薬が作成される")
        fun sharedGroupNotFound_OwnedMedicineIsCreated(isPublic: Boolean) {
            //given:
            val isWantToOwn = false

            //when:
            val actual = medicineDomainService.createMedicine(medicineId,
                                                              medicineName,
                                                              dosageAndAdministration,
                                                              effects,
                                                              precautions,
                                                              isPublic,
                                                              registeredAt,
                                                              accountId,
                                                              isWantToOwn)

            //then:
            assertThat(actual.owner).isEqualTo(MedicineOwner.create(accountId))
            assertThat(actual.isPublic).isEqualTo(isPublic)
        }
    }
}