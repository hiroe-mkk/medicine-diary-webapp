package example.infrastructure.db.query.medicine

import example.application.query.medicine.*
import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.medicine.*
import example.domain.model.sharedgroup.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import java.time.format.*

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
        val actual = jsonMedicineOverviewsQueryService.getMedicineOverviews(MedicineFilter("頭痛"), userSession)

        //then:
        assertThat(actual.ownedMedicines).extracting("medicineId")
            .containsExactlyInAnyOrder(*ownedMedicines.map { it.id.toString() }.toTypedArray())
        assertThat(actual.sharedGroupMedicines).extracting("medicineId")
            .containsExactlyInAnyOrder(*sharedGroupMedicines.map { it.id.toString() }.toTypedArray())
        val expectMemberMedicineIds = membersMedicines.filter { it.isPublic }.map { it.id.toString() }
        assertThat(actual.membersMedicines).extracting("medicineId")
            .containsExactlyInAnyOrder(*expectMemberMedicineIds.toTypedArray())
    }

    @Test
    @DisplayName("服用可能な薬概要一覧を取得する")
    fun findAvailableMedicineOverviews() {
        //when:
        val actual = jsonMedicineOverviewsQueryService.getAvailableMedicineOverviews(userSession)

        //then:
        assertThat(actual.medicines).extracting("medicineId")
            .containsExactlyInAnyOrder(*ownedMedicines.map { it.id.toString() }.toTypedArray(),
                                       *sharedGroupMedicines.map { it.id.toString() }.toTypedArray())
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
        val jsonInventory = medicine.inventory?.let {
            val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
            val startedOn = it.startedOn?.let { startedOn -> dateTimeFormatter.format(startedOn) }
            val expirationOn = it.expirationOn?.let { expirationOn -> dateTimeFormatter.format(expirationOn) }
            JSONInventory(it.remainingQuantity,
                          it.quantityPerPackage,
                          startedOn,
                          expirationOn,
                          it.unusedPackage)
        }
        return JSONMedicineOverview(medicine.id.toString(),
                                    medicine.medicineName.toString(),
                                    medicine.medicineImageURL?.toString(),
                                    medicine.isPublic,
                                    jsonDosageAndAdministration,
                                    medicine.effects.values,
                                    jsonInventory)
    }
}
