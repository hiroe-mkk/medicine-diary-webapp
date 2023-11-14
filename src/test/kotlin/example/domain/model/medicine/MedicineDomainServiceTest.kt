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

    private lateinit var requesterAccountId: AccountId
    private lateinit var user1AccountId: AccountId

    @BeforeEach
    internal fun setUp() {
        requesterAccountId = testAccountInserter.insertAccountAndProfile().first.id
        user1AccountId = testAccountInserter.insertAccountAndProfile().first.id
    }

    @Nested
    inner class GetOwnedMedicineTest {
        @Test
        @DisplayName("所有している薬を取得する")
        fun getOwnedMedicine() {
            //given:
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(requesterAccountId))

            //when:
            val actual = medicineDomainService.findOwnedMedicine(medicine.id, requesterAccountId)

            //then:
            assertThat(actual).usingRecursiveComparison().isEqualTo(medicine)
        }

        @Test
        @DisplayName("他のユーザーが所有する薬場合、薬の取得に失敗する")
        fun medicineOwnedByAnotherUser_gettingMedicineFails() {
            //given:
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(user1AccountId))

            //when:
            val actual = medicineDomainService.findOwnedMedicine(medicine.id, requesterAccountId)

            //then:
            assertThat(actual).isNull()
        }

        @Test
        @DisplayName("薬が見つからなかった場合、薬の取得に失敗する")
        fun medicineNotFound_gettingMedicineFails() {
            //given:
            val badMedicineId = MedicineId("NonexistentId")

            //when:
            val actual = medicineDomainService.findOwnedMedicine(badMedicineId, requesterAccountId)

            //then:
            assertThat(actual).isNull()
        }
    }

    @Nested
    inner class GetAvailableMedicineTest {
        @Test
        @DisplayName("所有している薬を取得する")
        fun getOwnedMedicine() {
            //given:
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(requesterAccountId))

            //when:
            val actual = medicineDomainService.findAvailableMedicine(medicine.id, requesterAccountId)

            //then:
            assertThat(actual).usingRecursiveComparison().isEqualTo(medicine)
        }

        @Test
        @DisplayName("他のユーザーが所有する薬場合、薬の取得に失敗する")
        fun medicineOwnedByAnotherUser_gettingMedicineFails() {
            //given:
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(user1AccountId))

            //when:
            val actual = medicineDomainService.findAvailableMedicine(medicine.id, requesterAccountId)

            //then:
            assertThat(actual).isNull()
        }

        @Test
        @DisplayName("共有グループの薬を取得する")
        fun getSharedGroupMedicine() {
            //given:
            val sharedGroupId = testSharedGroupInserter.insert(members = setOf(requesterAccountId)).id
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(sharedGroupId))

            //when:
            val actual = medicineDomainService.findAvailableMedicine(medicine.id, requesterAccountId)

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
            val actual = medicineDomainService.findAvailableMedicine(medicine.id, requesterAccountId)

            //then:
            assertThat(actual).isNull()
        }

        @Test
        @DisplayName("薬が見つからなかった場合、薬の取得に失敗する")
        fun medicineNotFound_gettingMedicineFails() {
            //given:
            val badMedicineId = MedicineId("NonexistentId")

            //when:
            val actual = medicineDomainService.findAvailableMedicine(badMedicineId, requesterAccountId)

            //then:
            assertThat(actual).isNull()
        }
    }

    @Nested
    inner class GetViewableMedicine {
        @Test
        @DisplayName("所有している薬を取得する")
        fun getOwnedMedicine() {
            //given:
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(requesterAccountId))

            //when:
            val actual = medicineDomainService.findViewableMedicine(medicine.id, requesterAccountId)

            //then:
            assertThat(actual).usingRecursiveComparison().isEqualTo(medicine)
        }

        @Test
        @DisplayName("他のユーザーが所有する薬場合、薬の取得に失敗する")
        fun medicineOwnedByAnotherUser_gettingMedicineFails() {
            //given:
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(user1AccountId))

            //when:
            val actual = medicineDomainService.findViewableMedicine(medicine.id, requesterAccountId)

            //then:
            assertThat(actual).isNull()
        }

        @Test
        @DisplayName("共有グループの薬を取得する")
        fun getSharedGroupMedicine() {
            //given:
            val sharedGroupId = testSharedGroupInserter.insert(members = setOf(requesterAccountId)).id
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(sharedGroupId))

            //when:
            val actual = medicineDomainService.findViewableMedicine(medicine.id, requesterAccountId)

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
            val actual = medicineDomainService.findViewableMedicine(medicine.id, requesterAccountId)

            //then:
            assertThat(actual).isNull()
        }

        @Test
        @DisplayName("メンバーの薬を取得する")
        fun getMemberMedicine() {
            //given:
            testSharedGroupInserter.insert(members = setOf(requesterAccountId, user1AccountId)).id
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(user1AccountId),
                                                       isPublic = true)

            //when:
            val actual = medicineDomainService.findViewableMedicine(medicine.id, requesterAccountId)

            //then:
            assertThat(actual).usingRecursiveComparison().isEqualTo(medicine)
        }

        @Test
        @DisplayName("メンバーの薬が非公開の場合、薬の取得に失敗する")
        fun medicineOwnedByMemberIsPrivate_gettingMedicineFails() {
            //given:
            testSharedGroupInserter.insert(members = setOf(requesterAccountId, user1AccountId)).id
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(user1AccountId),
                                                       isPublic = false)

            //when:
            val actual = medicineDomainService.findViewableMedicine(medicine.id, requesterAccountId)

            //then:
            assertThat(actual).isNull()
        }

        @Test
        @DisplayName("薬が見つからなかった場合、薬の取得に失敗する")
        fun medicineNotFound_gettingMedicineFails() {
            //given:
            val badMedicineId = MedicineId("NonexistentId")

            //when:
            val actual = medicineDomainService.findViewableMedicine(badMedicineId, requesterAccountId)

            //then:
            assertThat(actual).isNull()
        }
    }

    @Nested
    inner class GetAllMedicines {
        private lateinit var memberAccountId: AccountId

        private lateinit var requesterMedicineWithPublic: Medicine
        private lateinit var requesterMedicineWithPrivate: Medicine

        private lateinit var user1MedicineWithPublic: Medicine
        private lateinit var user1MedicineWithPrivate: Medicine

        private lateinit var memberMedicineWithPublic: Medicine
        private lateinit var memberMedicineWithPrivate: Medicine

        private lateinit var participatingSharedGroupMedicine: Medicine
        private lateinit var nonParticipatingSharedGroupMedicine: Medicine

        @BeforeEach
        internal fun setUp() {
            memberAccountId = testAccountInserter.insertAccountAndProfile().first.id

            val participatingSharedGroupId = createSharedGroup(requesterAccountId, memberAccountId)
            val nonParticipatingSharedGroupId = createSharedGroup(user1AccountId)

            requesterMedicineWithPublic =
                    testMedicineInserter.insert(owner = MedicineOwner.create(requesterAccountId), isPublic = true)
            requesterMedicineWithPrivate =
                    testMedicineInserter.insert(owner = MedicineOwner.create(requesterAccountId), isPublic = false)
            user1MedicineWithPublic =
                    testMedicineInserter.insert(owner = MedicineOwner.create(user1AccountId), isPublic = true)
            user1MedicineWithPrivate =
                    testMedicineInserter.insert(owner = MedicineOwner.create(user1AccountId), isPublic = false)
            memberMedicineWithPublic =
                    testMedicineInserter.insert(owner = MedicineOwner.create(memberAccountId), isPublic = true)
            memberMedicineWithPrivate =
                    testMedicineInserter.insert(owner = MedicineOwner.create(memberAccountId), isPublic = false)
            participatingSharedGroupMedicine =
                    testMedicineInserter.insert(owner = MedicineOwner.create(participatingSharedGroupId))
            nonParticipatingSharedGroupMedicine =
                    testMedicineInserter.insert(owner = MedicineOwner.create(nonParticipatingSharedGroupId))
        }

        @Test
        @DisplayName("所有する薬一覧を取得する")
        fun getOwnedMedicines() {
            //when:
            val actual = medicineDomainService.findAllOwnedMedicines(requesterAccountId)

            //then:
            assertThat(actual)
                .extracting("id")
                .containsExactlyInAnyOrder(requesterMedicineWithPublic.id, requesterMedicineWithPrivate.id)
        }

        @Test
        @DisplayName("共有グループの薬一覧を取得する")
        fun getSharedGroupMedicines() {
            //when:
            val actual = medicineDomainService.findAllSharedGroupMedicines(requesterAccountId)

            //then:
            assertThat(actual)
                .extracting("id")
                .containsExactlyInAnyOrder(participatingSharedGroupMedicine.id)
        }

        @Test
        @DisplayName("服用可能な薬一覧を取得する")
        fun getAvailableMedicines() {
            //when:
            val actual = medicineDomainService.findAllAvailableMedicines(requesterAccountId)

            //then:
            assertThat(actual)
                .extracting("id")
                .containsExactlyInAnyOrder(requesterMedicineWithPublic.id,
                                           requesterMedicineWithPrivate.id,
                                           participatingSharedGroupMedicine.id)
        }

        @Test
        @DisplayName("メンバーの薬一覧を取得する")
        fun getMembersMedicines() {
            //when:
            val actual = medicineDomainService.findAllMembersMedicines(requesterAccountId)

            //then:
            assertThat(actual)
                .extracting("id")
                .containsExactlyInAnyOrder(memberMedicineWithPublic.id)
        }

        @Test
        @DisplayName("閲覧可能な薬一覧を取得する")
        fun getViewableMedicines() {
            //when:
            val actual = medicineDomainService.findAllViewableMedicines(requesterAccountId)

            //then:
            assertThat(actual)
                .extracting("id")
                .containsExactlyInAnyOrder(requesterMedicineWithPublic.id,
                                           requesterMedicineWithPrivate.id,
                                           memberMedicineWithPublic.id,
                                           participatingSharedGroupMedicine.id)
        }

        private fun createSharedGroup(vararg accountId: AccountId) =
                testSharedGroupInserter.insert(members = setOf(*accountId)).id
    }
}