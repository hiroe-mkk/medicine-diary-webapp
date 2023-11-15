package example.domain.model.takingrecord

import example.application.service.takingrecord.*
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

@DomainLayerTest
internal class TakingRecordQueryServiceTest(@Autowired private val takingRecordQueryService: TakingRecordQueryService,
                                            @Autowired private val testAccountInserter: TestAccountInserter,
                                            @Autowired private val testMedicineInserter: TestMedicineInserter,
                                            @Autowired private val testTakingRecordInserter: TestTakingRecordInserter) {
    private lateinit var requesterAccountId: AccountId
    private lateinit var requesterMedicine: Medicine

    @BeforeEach
    internal fun setUp() {
        requesterAccountId = testAccountInserter.insertAccountAndProfile().first.id
        requesterMedicine = testMedicineInserter.insert(MedicineOwner.create(requesterAccountId))
    }

    @Test
    @DisplayName("服用記録を取得する")
    fun getTakingRecord() {
        //given:
        val takingRecord = testTakingRecordInserter.insert(requesterAccountId, requesterMedicine.id)

        //when:
        val actual = takingRecordQueryService.findOwnedTakingRecord(takingRecord.id, requesterAccountId)

        //then:
        assertThat(actual).usingRecursiveComparison().isEqualTo(takingRecord)
    }

    @Test
    @DisplayName("他のユーザーが記録した服用記録の場合、服用記録の取得に失敗する")
    fun takingRecordRecordedByAnotherUser_gettingTakingRecordFails() {
        //given:
        val user1AccountId = testAccountInserter.insertAccountAndProfile().first.id
        val takingRecord = testTakingRecordInserter.insert(user1AccountId, requesterMedicine.id)

        //when:
        val actual = takingRecordQueryService.findOwnedTakingRecord(takingRecord.id, requesterAccountId)

        //then:
        assertThat(actual).isNull()
    }
}