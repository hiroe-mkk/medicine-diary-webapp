package example.infrastructure.db.query.medicine

import example.application.query.medicine.*
import example.application.shared.usersession.*
import example.domain.model.medicine.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@MyBatisQueryServiceTest
internal class MyBatisJSONMedicineOverviewsQueryServiceTest(@Autowired private val jsonMedicineOverviewsQueryService: JSONMedicineOverviewsQueryService,
                                                            @Autowired private val testAccountInserter: TestAccountInserter,
                                                            @Autowired private val testMedicineInserter: TestMedicineInserter,
                                                            @Autowired private val testSharedGroupInserter: TestSharedGroupInserter) {
    private lateinit var userSession: UserSession

    private lateinit var requesterPublicMedicineId: MedicineId
    private lateinit var requesterPrivateMedicineId: MedicineId
    private lateinit var memberPublicMedicineId: MedicineId
    private lateinit var memberPrivateMedicineId: MedicineId
    private lateinit var sharedGroupMedicineId: MedicineId

    @BeforeEach
    internal fun setUp() {
        val requesterAccountId = testAccountInserter.insertAccountAndProfile().first.id
        userSession = UserSessionFactory.create(requesterAccountId)
        val memberAccountId = testAccountInserter.insertAccountAndProfile().first.id
        val sharedGroupId = testSharedGroupInserter.insert(members = setOf(userSession.accountId, memberAccountId)).id

        val requester = MedicineOwner.create(userSession.accountId)
        requesterPublicMedicineId = testMedicineInserter.insert(owner = requester, isPublic = true).id
        requesterPrivateMedicineId = testMedicineInserter.insert(owner = requester, isPublic = false).id

        val member = MedicineOwner.create(memberAccountId)
        memberPublicMedicineId = testMedicineInserter.insert(owner = member, isPublic = true).id
        memberPrivateMedicineId = testMedicineInserter.insert(owner = member, isPublic = false).id

        val sharedGroup = MedicineOwner.create(sharedGroupId)
        sharedGroupMedicineId = testMedicineInserter.insert(owner = sharedGroup, isPublic = true).id
    }

    @Test
    @DisplayName("薬概要一覧を取得する")
    fun getMedicineOverviews() {
        //when:
        val actual = jsonMedicineOverviewsQueryService.getMedicineOverviews(MedicineFilter(""),
                                                                            userSession)

        //then:
        assertThat(actual.ownedMedicines).extracting("medicineId")
            .containsExactlyInAnyOrder(requesterPublicMedicineId.value, requesterPrivateMedicineId.value)
        assertThat(actual.sharedGroupMedicines).extracting("medicineId")
            .containsExactlyInAnyOrder(sharedGroupMedicineId.value)
        assertThat(actual.membersMedicines).extracting("medicineId")
            .containsExactlyInAnyOrder(memberPublicMedicineId.value)
    }

    @Test
    @DisplayName("フィルタリングされた薬概要一覧を取得する")
    fun getFilteredMedicineOverviews() {
        //given:
        val requester = MedicineOwner.create(userSession.accountId)
        val stomachacheMedicineId = testMedicineInserter.insert(owner = requester,
                                                                effects = Effects(listOf("腹痛"))).id

        //when:
        val actual = jsonMedicineOverviewsQueryService.getMedicineOverviews(MedicineFilter("腹痛"),
                                                                            userSession)

        //then:
        assertThat(actual.ownedMedicines).extracting("medicineId")
            .containsExactlyInAnyOrder(stomachacheMedicineId.value)
        assertThat(actual.sharedGroupMedicines).isEmpty()
        assertThat(actual.membersMedicines).isEmpty()
    }

    @Test
    @DisplayName("服用可能な薬概要一覧を取得する")
    fun getAvailableMedicineOverviews() {
        //when:
        val actual = jsonMedicineOverviewsQueryService.getAvailableMedicineOverviews(userSession)

        //then:
        assertThat(actual.medicines).extracting("medicineId")
            .containsExactlyInAnyOrder(requesterPublicMedicineId.value,
                                       requesterPrivateMedicineId.value,
                                       sharedGroupMedicineId.value)
    }
}
