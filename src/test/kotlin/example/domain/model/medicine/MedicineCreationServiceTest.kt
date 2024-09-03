package example.domain.model.medicine

import example.domain.model.account.*
import example.infrastructure.db.repository.shared.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.junit.jupiter.params.*
import org.junit.jupiter.params.provider.*
import org.springframework.beans.factory.annotation.*
import java.time.*

@DomainLayerTest
internal class MedicineCreationServiceTest(@Autowired private val medicineCreationService: MedicineCreationService,
                                           @Autowired private val testAccountInserter: TestAccountInserter,
                                           @Autowired private val testSharedGroupInserter: TestSharedGroupInserter) {
    private lateinit var requesterAccountId: AccountId
    private lateinit var user1AccountId: AccountId

    @BeforeEach
    internal fun setUp() {
        requesterAccountId = testAccountInserter.insertAccountAndProfile().first.id
        user1AccountId = testAccountInserter.insertAccountAndProfile().first.id
    }

    private val medicineId = MedicineId(EntityIdHelper.generate())
    private val medicineName: MedicineName = MedicineName("ロキソニンS")
    private val dosageAndAdministration: DosageAndAdministration = DosageAndAdministration(Dose(1.0),
                                                                                           "錠",
                                                                                           3,
                                                                                           emptyList())
    private val effects: Effects = Effects(listOf("頭痛", "解熱"))
    private val precautions: String = "服用間隔は4時間以上開けること。"
    private val registeredAt = LocalDateTime.of(2020, 1, 1, 0, 0)

    @ParameterizedTest
    @CsvSource("true", "false")
    @DisplayName("所有する薬を作成する")
    fun createOwnedMedicine(isPublic: Boolean) {
        //when:
        val actual = medicineCreationService.create(medicineId,
                                                    medicineName,
                                                    dosageAndAdministration,
                                                    effects,
                                                    precautions,
                                                    false,
                                                    isPublic,
                                                    registeredAt,
                                                    requesterAccountId)

        //then:
        assertThat(actual.owner).isEqualTo(MedicineOwner.create(requesterAccountId))
        assertThat(actual.isPublic).isEqualTo(isPublic)
    }

    @ParameterizedTest
    @CsvSource("true", "false")
    @DisplayName("共有グループの薬を作成する")
    fun createSharedGroupMedicine(isPublic: Boolean) {
        //given:
        val sharedGroupId = testSharedGroupInserter.insert(members = setOf(requesterAccountId)).id
        val isOwnedBySharedGroup = true

        //when:
        val actual = medicineCreationService.create(medicineId,
                                                    medicineName,
                                                    dosageAndAdministration,
                                                    effects,
                                                    precautions,
                                                    true,
                                                    isPublic,
                                                    registeredAt,
                                                    requesterAccountId)

        //then:
        assertThat(actual.owner).isEqualTo(MedicineOwner.create(sharedGroupId))
        assertThat(actual.isPublic).isTrue
    }

    @ParameterizedTest
    @CsvSource("true", "false")
    @DisplayName("共有グループが見つからなかった場合、所有する薬が作成される")
    fun sharedGroupNotFound_OwnedMedicineIsCreated(isPublic: Boolean) {
        //when:
        val actual = medicineCreationService.create(medicineId,
                                                    medicineName,
                                                    dosageAndAdministration,
                                                    effects,
                                                    precautions,
                                                    true,
                                                    isPublic,
                                                    registeredAt,
                                                    requesterAccountId)

        //then:
        assertThat(actual.owner).isEqualTo(MedicineOwner.create(requesterAccountId))
        assertThat(actual.isPublic).isEqualTo(isPublic)
    }
}
