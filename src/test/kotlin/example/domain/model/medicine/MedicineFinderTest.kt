package example.domain.model.medicine

import example.domain.model.account.*
import example.infrastructure.db.repository.shared.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@DomainLayerTest
class MedicineFinderTest(@Autowired private val medicineFinder: MedicineFinder,
                         @Autowired private val testMedicineInserter: TestMedicineInserter,
                         @Autowired private val testAccountInserter: TestAccountInserter,
                         @Autowired private val testSharedGroupInserter: TestSharedGroupInserter) {
    private lateinit var requesterAccountId: AccountId
    private lateinit var user1AccountId: AccountId

    @BeforeEach
    internal fun setUp() {
        requesterAccountId = testAccountInserter.insertAccountAndProfile().first.id
        user1AccountId = testAccountInserter.insertAccountAndProfile().first.id
    }

    @Nested
    inner class GetAvailableMedicineTest {
        @Test
        @DisplayName("所有している薬を取得する")
        fun getOwnedMedicine() {
            //given:
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(requesterAccountId))

            //when:
            val actual = medicineFinder.findAvailableMedicine(medicine.id, requesterAccountId)

            //then:
            assertThat(actual).usingRecursiveComparison().isEqualTo(medicine)
        }

        @Test
        @DisplayName("他のユーザーが所有する薬場合、薬の取得に失敗する")
        fun medicineOwnedByAnotherUser_gettingMedicineFails() {
            //given:
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(user1AccountId))

            //when:
            val actual = medicineFinder.findAvailableMedicine(medicine.id, requesterAccountId)

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
            val actual = medicineFinder.findAvailableMedicine(medicine.id, requesterAccountId)

            //then:
            assertThat(actual).usingRecursiveComparison().isEqualTo(medicine)
        }

        @Test
        @DisplayName("参加していない共有グループが所有する薬の場合、薬の取得に失敗する")
        fun medicineOwnedByUnjoinedSharedGroup_gettingMedicineFails() {
            //given:
            val sharedGroupId = testSharedGroupInserter.insert(members = setOf(user1AccountId)).id
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(sharedGroupId))

            //when:
            val actual = medicineFinder.findAvailableMedicine(medicine.id, requesterAccountId)

            //then:
            assertThat(actual).isNull()
        }

        @Test
        @DisplayName("薬が見つからなかった場合、薬の取得に失敗する")
        fun medicineNotFound_gettingMedicineFails() {
            //given:
            val nonexistentMedicineId = MedicineId(EntityIdHelper.generate())

            //when:
            val actual = medicineFinder.findAvailableMedicine(nonexistentMedicineId, requesterAccountId)

            //then:
            assertThat(actual).isNull()
        }
    }

    @Nested
    inner class GetViewableMedicineTest {
        @Test
        @DisplayName("所有している薬を取得する")
        fun getOwnedMedicine() {
            //given:
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(requesterAccountId))

            //when:
            val actual = medicineFinder.findViewableMedicine(medicine.id, requesterAccountId)

            //then:
            assertThat(actual).usingRecursiveComparison().isEqualTo(medicine)
        }

        @Test
        @DisplayName("他のユーザーが所有する薬場合、薬の取得に失敗する")
        fun medicineOwnedByAnotherUser_gettingMedicineFails() {
            //given:
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(user1AccountId))

            //when:
            val actual = medicineFinder.findViewableMedicine(medicine.id, requesterAccountId)

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
            val actual = medicineFinder.findViewableMedicine(medicine.id, requesterAccountId)

            //then:
            assertThat(actual).usingRecursiveComparison().isEqualTo(medicine)
        }

        @Test
        @DisplayName("参加していない共有グループが所有する薬の場合、薬の取得に失敗する")
        fun medicineOwnedByUnjoinedSharedGroup_gettingMedicineFails() {
            //given:
            val sharedGroupId = testSharedGroupInserter.insert(members = setOf(user1AccountId)).id
            val medicine = testMedicineInserter.insert(owner = MedicineOwner.create(sharedGroupId))

            //when:
            val actual = medicineFinder.findViewableMedicine(medicine.id, requesterAccountId)

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
            val actual = medicineFinder.findViewableMedicine(medicine.id, requesterAccountId)

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
            val actual = medicineFinder.findViewableMedicine(medicine.id, requesterAccountId)

            //then:
            assertThat(actual).isNull()
        }

        @Test
        @DisplayName("薬が見つからなかった場合、薬の取得に失敗する")
        fun medicineNotFound_gettingMedicineFails() {
            //given:
            val nonexistentMedicineId = MedicineId(EntityIdHelper.generate())

            //when:
            val actual = medicineFinder.findViewableMedicine(nonexistentMedicineId, requesterAccountId)

            //then:
            assertThat(actual).isNull()
        }
    }

    @Nested
    inner class GetAllMedicinesTest {
        private lateinit var memberAccountId: AccountId

        private lateinit var requesterMedicineWithPublic: Medicine
        private lateinit var requesterMedicineWithPrivate: Medicine

        private lateinit var user1MedicineWithPublic: Medicine
        private lateinit var user1MedicineWithPrivate: Medicine

        private lateinit var memberMedicineWithPublic: Medicine
        private lateinit var memberMedicineWithPrivate: Medicine

        private lateinit var joinedSharedGroupMedicine: Medicine
        private lateinit var unjoinedSharedGroupMedicine: Medicine

        @BeforeEach
        internal fun setUp() {
            memberAccountId = testAccountInserter.insertAccountAndProfile().first.id

            val joinedSharedGroupId = createSharedGroup(requesterAccountId, memberAccountId)
            val unjoinedSharedGroupId = createSharedGroup(user1AccountId)

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
            joinedSharedGroupMedicine =
                    testMedicineInserter.insert(owner = MedicineOwner.create(joinedSharedGroupId))
            unjoinedSharedGroupMedicine =
                    testMedicineInserter.insert(owner = MedicineOwner.create(unjoinedSharedGroupId))
        }

        @Test
        @DisplayName("所有する薬一覧を取得する")
        fun getOwnedMedicines() {
            //when:
            val actual = medicineFinder.findAllOwnedMedicines(requesterAccountId)

            //then:
            assertThat(actual)
                .extracting("id")
                .containsExactlyInAnyOrder(requesterMedicineWithPublic.id, requesterMedicineWithPrivate.id)
        }

        @Test
        @DisplayName("共有グループの薬一覧を取得する")
        fun getSharedGroupMedicines() {
            //when:
            val actual = medicineFinder.findAllSharedGroupMedicines(requesterAccountId)

            //then:
            assertThat(actual)
                .extracting("id")
                .containsExactlyInAnyOrder(joinedSharedGroupMedicine.id)
        }

        @Test
        @DisplayName("服用可能な薬一覧を取得する")
        fun getAvailableMedicines() {
            //when:
            val actual = medicineFinder.findAllAvailableMedicines(requesterAccountId)

            //then:
            assertThat(actual)
                .extracting("id")
                .containsExactlyInAnyOrder(requesterMedicineWithPublic.id,
                                           requesterMedicineWithPrivate.id,
                                           joinedSharedGroupMedicine.id)
        }

        @Test
        @DisplayName("メンバーの薬一覧を取得する")
        fun getMembersMedicines() {
            //when:
            val actual = medicineFinder.findAllMembersMedicines(requesterAccountId)

            //then:
            assertThat(actual)
                .extracting("id")
                .containsExactlyInAnyOrder(memberMedicineWithPublic.id)
        }

        @Test
        @DisplayName("閲覧可能な薬一覧を取得する")
        fun getViewableMedicines() {
            //when:
            val actual = medicineFinder.findAllViewableMedicines(requesterAccountId)

            //then:
            assertThat(actual)
                .extracting("id")
                .containsExactlyInAnyOrder(requesterMedicineWithPublic.id,
                                           requesterMedicineWithPrivate.id,
                                           memberMedicineWithPublic.id,
                                           joinedSharedGroupMedicine.id)
        }

        private fun createSharedGroup(vararg accountId: AccountId) =
                testSharedGroupInserter.insert(members = setOf(*accountId)).id
    }
}
