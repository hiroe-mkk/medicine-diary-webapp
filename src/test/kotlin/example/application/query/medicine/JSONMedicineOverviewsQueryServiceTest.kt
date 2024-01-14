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

    @BeforeEach
    internal fun setUp() {
        val requesterAccountId = testAccountInserter.insertAccountAndProfile().first.id
        userSession = UserSessionFactory.create(requesterAccountId)
        memberAccountId = testAccountInserter.insertAccountAndProfile().first.id
        sharedGroupId = testSharedGroupInserter.insert(members = setOf(userSession.accountId, memberAccountId)).id
    }

    @Test
    @DisplayName("服用可能な薬概要一覧を取得する")
    fun findAvailableMedicineOverviews() {
        //given:
        val medicineOwner1 = MedicineOwner.create(userSession.accountId)
        val localDateTime = LocalDateTime.of(2020, 1, 1, 0, 0)
        val ownedMedicine1 = testMedicineInserter.insert(owner = medicineOwner1,
                                                         registeredAt = localDateTime)
        val ownedMedicine2 = testMedicineInserter.insert(owner = medicineOwner1,
                                                         registeredAt = localDateTime.plusDays(1))
        val medicineOwner2 = MedicineOwner.create(sharedGroupId)
        val sharedGroupMedicine = testMedicineInserter.insert(owner = medicineOwner2,
                                                              registeredAt = localDateTime.plusDays(2))
        val medicineOwner3 = MedicineOwner.create(memberAccountId)
        val memberMedicine = testMedicineInserter.insert(owner = medicineOwner3)

        //when:
        val actual = jsonMedicineOverviewsQueryService.findJSONAvailableMedicineOverviews(userSession)

        //then:
        assertThat(actual.medicines).extracting("medicineId").containsExactly(sharedGroupMedicine.id.toString(),
                                                                              ownedMedicine2.id.toString(),
                                                                              ownedMedicine1.id.toString())
    }
}