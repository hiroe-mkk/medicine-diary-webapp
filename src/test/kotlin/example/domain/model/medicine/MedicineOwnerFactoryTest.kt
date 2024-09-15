package example.domain.model.medicine

import example.domain.model.account.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.junit.jupiter.params.*
import org.junit.jupiter.params.provider.*
import org.springframework.beans.factory.annotation.*

@DomainLayerTest
class MedicineOwnerFactoryTest(@Autowired private val medicineOwnerFactory: MedicineOwnerFactory,
                               @Autowired private val testAccountInserter: TestAccountInserter,
                               @Autowired private val testSharedGroupInserter: TestSharedGroupInserter) {
    private lateinit var accountId: AccountId

    @BeforeEach
    internal fun setUp() {
        accountId = testAccountInserter.insertAccountAndProfile().first.id
    }

    @ParameterizedTest
    @CsvSource("true", "false")
    @DisplayName("所有する薬を作成する")
    fun createOwnedMedicine(isPublic: Boolean) {
        //when:
        val actual = medicineOwnerFactory.create(accountId, false)

        //then:
        assertThat(actual).isEqualTo(MedicineOwner.create(accountId))
    }

    @ParameterizedTest
    @CsvSource("true", "false")
    @DisplayName("共有グループの薬を作成する")
    fun createSharedGroupMedicine(isPublic: Boolean) {
        //given:
        val sharedGroupId = testSharedGroupInserter.insert(members = setOf(accountId)).id

        //when:
        val actual = medicineOwnerFactory.create(accountId, true)

        //then:
        assertThat(actual).isEqualTo(MedicineOwner.create(sharedGroupId))
    }

    @ParameterizedTest
    @CsvSource("true", "false")
    @DisplayName("共有グループが見つからなかった場合、所有する薬が作成される")
    fun sharedGroupNotFound_OwnedMedicineIsCreated(isPublic: Boolean) {
        //when:
        val actual = medicineOwnerFactory.create(accountId, true)

        //then:
        assertThat(actual).isEqualTo(MedicineOwner.create(accountId))
    }
}
