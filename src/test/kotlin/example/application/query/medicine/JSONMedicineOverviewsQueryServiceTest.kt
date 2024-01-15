package example.application.query.medicine

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
internal class JSONMedicineOverviewsQueryServiceTest(@Autowired private val jsonMedicineOverviewsQueryService: JSONMedicineOverviewsQueryService,
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
        val actual = jsonMedicineOverviewsQueryService.findMedicineOverviews(userSession)

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
        val actual = jsonMedicineOverviewsQueryService.findJSONAvailableMedicineOverviews(userSession)

        //then:
        assertThat(actual.medicines).extracting("medicineId")
            .containsExactlyInAnyOrder(*ownedMedicines.map { it.id.toString() }.toTypedArray(),
                                       *sharedGroupMedicines.map { it.id.toString() }.toTypedArray())
    }

    private fun createMedicines(medicineOwner: MedicineOwner): List<Medicine> {
        return listOf(testMedicineInserter.insert(owner = medicineOwner, isPublic = true),
                      testMedicineInserter.insert(owner = medicineOwner, isPublic = false))
    }
}