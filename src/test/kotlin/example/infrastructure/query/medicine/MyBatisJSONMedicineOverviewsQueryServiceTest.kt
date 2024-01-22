package example.infrastructure.query.medicine

import example.application.query.medicine.*
import example.application.service.medicine.*
import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.medicine.*
import example.domain.model.sharedgroup.*
import example.domain.shared.type.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import io.mockk.*
import org.assertj.core.api.Assertions.*
import org.bouncycastle.asn1.x500.style.RFC4519Style.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import java.time.*

@MyBatisQueryServiceTest
internal class MyBatisJSONMedicineOverviewsQueryServiceTest(@Autowired private val jsonMedicineOverviewsQueryService: JSONMedicineOverviewsQueryService,
                                                            @Autowired private val testAccountInserter: TestAccountInserter,
                                                            @Autowired private val testMedicineInserter: TestMedicineInserter,
                                                            @Autowired private val testSharedGroupInserter: TestSharedGroupInserter) {
    private lateinit var userSession: UserSession
    private lateinit var sharedGroupId: SharedGroupId
    private lateinit var memberAccountId: AccountId

    private lateinit var ownedMedicines: List<Medicine>
    private lateinit var sharedGroupMedicines: List<Medicine>
    private lateinit var membersMedicines: List<Medicine>

    @BeforeEach
    internal fun setUp() {
        val requesterAccountId = testAccountInserter.insertAccountAndProfile().first.id
        userSession = UserSessionFactory.create(requesterAccountId)
        memberAccountId = testAccountInserter.insertAccountAndProfile().first.id
        sharedGroupId = testSharedGroupInserter.insert(members = setOf(userSession.accountId, memberAccountId)).id

        ownedMedicines = createMedicines(MedicineOwner.create(userSession.accountId))
        sharedGroupMedicines = createMedicines(MedicineOwner.create(sharedGroupId))
        membersMedicines = createMedicines(MedicineOwner.create(memberAccountId))
    }

    @Test
    @DisplayName("薬概要一覧を取得する")
    fun findMedicineOverviews() {
        //when:
        val actual = jsonMedicineOverviewsQueryService.findMedicineOverviews(userSession,
                                                                             MedicineFilter("頭痛"))

        //then:
        assertThat(actual.ownedMedicines)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(*ownedMedicines.map { createJSONMedicineOverview(it) }.toTypedArray())
        assertThat(actual.sharedGroupMedicines)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(*sharedGroupMedicines.map { createJSONMedicineOverview(it) }.toTypedArray())
        val expectMembersMedicines = membersMedicines.filter { it.isPublic }
        assertThat(actual.membersMedicines)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(*expectMembersMedicines.map { createJSONMedicineOverview(it) }.toTypedArray())
    }

    @Test
    @DisplayName("服用可能な薬概要一覧を取得する")
    fun findAvailableMedicineOverviews() {
        //when:
        val actual = jsonMedicineOverviewsQueryService.findJSONAvailableMedicineOverviews(userSession)

        //then:
        assertThat(actual.medicines)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(*ownedMedicines.map { createJSONMedicineOverview(it) }.toTypedArray(),
                                       *sharedGroupMedicines.map { createJSONMedicineOverview(it) }.toTypedArray())
    }

    private fun createMedicines(medicineOwner: MedicineOwner): List<Medicine> {
        return listOf(testMedicineInserter.insert(owner = medicineOwner, isPublic = true),
                      testMedicineInserter.insert(owner = medicineOwner, isPublic = false))
    }

    private fun createJSONMedicineOverview(medicine: Medicine): JSONMedicineOverview {
        val jsonDosageAndAdministration =
                JSONDosageAndAdministration(medicine.dosageAndAdministration.dose.quantity.toString(),
                                            medicine.dosageAndAdministration.doseUnit,
                                            medicine.dosageAndAdministration.timesPerDay.toString(),
                                            medicine.dosageAndAdministration.timingOptions)
        return JSONMedicineOverview(medicine.id.toString(),
                                    medicine.medicineName.toString(),
                                    medicine.medicineImageURL?.toString(),
                                    medicine.isPublic,
                                    jsonDosageAndAdministration,
                                    medicine.effects.values)
    }
}