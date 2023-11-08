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

    private lateinit var userAccountId: AccountId
    private lateinit var anotherUserAccountId: AccountId

    @BeforeEach
    internal fun setUp() {
        userAccountId = testAccountInserter.insertAccountAndProfile().first.id
        anotherUserAccountId = testAccountInserter.insertAccountAndProfile().first.id
    }

    @Nested
    inner class GetOwnedMedicineTest {
        @Test
        @DisplayName("所有している薬を取得する")
        fun getOwnedMedicine() {
            //given:
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(userAccountId))

            //when:
            val actual = medicineDomainService.findOwnedMedicine(medicine.id, userAccountId)

            //then:
            assertThat(actual).usingRecursiveComparison().isEqualTo(medicine)
        }

        @Test
        @DisplayName("自分以外のユーザーが所有する薬場合、薬の取得に失敗する")
        fun medicineOwnedByAnotherUser_gettingMedicineFails() {
            //given:
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(anotherUserAccountId))

            //when:
            val actual = medicineDomainService.findOwnedMedicine(medicine.id, userAccountId)

            //then:
            assertThat(actual).isNull()
        }

        @Test
        @DisplayName("薬が見つからなかった場合、薬の取得に失敗する")
        fun medicineNotFound_gettingMedicineFails() {
            //given:
            val badMedicineId = MedicineId("NonexistentId")

            //when:
            val actual = medicineDomainService.findOwnedMedicine(badMedicineId, userAccountId)

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
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(userAccountId))

            //when:
            val actual = medicineDomainService.findUserMedicine(medicine.id, userAccountId)

            //then:
            assertThat(actual).usingRecursiveComparison().isEqualTo(medicine)
        }

        @Test
        @DisplayName("自分以外のユーザーが所有する薬場合、薬の取得に失敗する")
        fun medicineOwnedByAnotherUser_gettingMedicineFails() {
            //given:
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(anotherUserAccountId))

            //when:
            val actual = medicineDomainService.findUserMedicine(medicine.id, userAccountId)

            //then:
            assertThat(actual).isNull()
        }

        @Test
        @DisplayName("共有グループの薬を取得する")
        fun getSharedGroupMedicine() {
            //given:
            val sharedGroupId = testSharedGroupInserter.insert(members = setOf(userAccountId)).id
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(sharedGroupId))

            //when:
            val actual = medicineDomainService.findUserMedicine(medicine.id, userAccountId)

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
            val actual = medicineDomainService.findUserMedicine(medicine.id, userAccountId)

            //then:
            assertThat(actual).isNull()
        }

        @Test
        @DisplayName("薬が見つからなかった場合、薬の取得に失敗する")
        fun medicineNotFound_gettingMedicineFails() {
            //given:
            val badMedicineId = MedicineId("NonexistentId")

            //when:
            val actual = medicineDomainService.findUserMedicine(badMedicineId, userAccountId)

            //then:
            assertThat(actual).isNull()
        }
    }

    @Nested
    inner class GetAllUserMedicines {
        @Test
        @DisplayName("ユーザーの薬一覧を取得する")
        fun getUserMedicine() {
            //given:
            val ownedMedicine = testMedicineInserter.insert(owner = MedicineOwner.create(userAccountId))
            val anotherUserMedicine = testMedicineInserter.insert(owner = MedicineOwner.create(anotherUserAccountId))
            val participatingSharedGroupMedicine =
                    testMedicineInserter.insert(owner = MedicineOwner.create(createSharedGroup(userAccountId)))
            val nonParticipatingSharedGroupMedicine =
                    testMedicineInserter.insert(owner = MedicineOwner.create(createSharedGroup(anotherUserAccountId)))

            //when:
            val actual = medicineDomainService.findAllUserMedicines(userAccountId)

            //then:
            assertThat(actual)
                .extracting("id")
                .containsExactlyInAnyOrder(ownedMedicine.id, participatingSharedGroupMedicine.id)
        }

        private fun createSharedGroup(accountId: AccountId) =
                testSharedGroupInserter.insert(members = setOf(accountId)).id
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
                                                              userAccountId,
                                                              isWantToOwn)

            //then:
            assertThat(actual.owner).isEqualTo(MedicineOwner.create(userAccountId))
            assertThat(actual.isPublic).isEqualTo(isPublic)
        }

        @ParameterizedTest
        @CsvSource("true", "false")
        @DisplayName("共有グループの薬を作成する")
        fun createSharedGroupMedicine(isPublic: Boolean) {
            //given:
            val sharedGroupId = testSharedGroupInserter.insert(members = setOf(userAccountId)).id
            val isWantToOwn = false

            //when:
            val actual = medicineDomainService.createMedicine(medicineId,
                                                              medicineName,
                                                              dosageAndAdministration,
                                                              effects,
                                                              precautions,
                                                              isPublic,
                                                              registeredAt,
                                                              userAccountId,
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
                                                              userAccountId,
                                                              isWantToOwn)

            //then:
            assertThat(actual.owner).isEqualTo(MedicineOwner.create(userAccountId))
            assertThat(actual.isPublic).isEqualTo(isPublic)
        }
    }
}