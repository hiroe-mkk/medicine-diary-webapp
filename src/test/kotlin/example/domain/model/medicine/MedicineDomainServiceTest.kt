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
internal class MedicineDomainServiceTest(@Autowired private val sharedGroupRepository: SharedGroupRepository,
                                         @Autowired private val testAccountInserter: TestAccountInserter,
                                         @Autowired private val testSharedGroupInserter: TestSharedGroupInserter) {
    private val medicineDomainService: MedicineDomainService = MedicineDomainService(sharedGroupRepository)

    private lateinit var accountId: AccountId

    private val medicineId = MedicineId("medicineId")
    private val medicineName: MedicineName = MedicineName("ロキソニンS")
    private val dosageAndAdministration: DosageAndAdministration = DosageAndAdministration(Dose(1.0),
                                                                                           "錠",
                                                                                           3,
                                                                                           emptyList())
    private val effects: Effects = Effects(listOf("頭痛", "解熱"))
    private val precautions: Note = Note("服用間隔は4時間以上開けること。")
    private val registeredAt = LocalDateTime.of(2020, 1, 1, 0, 0)

    @BeforeEach
    internal fun setUp() {
        accountId = testAccountInserter.insertAccountAndProfile().first.id
    }

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