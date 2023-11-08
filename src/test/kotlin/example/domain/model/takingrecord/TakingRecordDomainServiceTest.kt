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

@MyBatisRepositoryTest
internal class TakingRecordDomainServiceTest(@Autowired private val takingRecordRepository: TakingRecordRepository,
                                             @Autowired private val testAccountInserter: TestAccountInserter,
                                             @Autowired private val testMedicineInserter: TestMedicineInserter,
                                             @Autowired private val testTakingRecordInserter: TestTakingRecordInserter) {
    private val takingRecordDomainService: TakingRecordDomainService = TakingRecordDomainService(takingRecordRepository)

    private lateinit var userAccountId: AccountId
    private lateinit var medicine: Medicine

    @BeforeEach
    internal fun setUp() {
        userAccountId = testAccountInserter.insertAccountAndProfile().first.id
        medicine = testMedicineInserter.insert(MedicineOwner.create(userAccountId))
    }

    @Test
    @DisplayName("服用記録を取得する")
    fun getTakingRecord() {
        //given:
        val takingRecord = testTakingRecordInserter.insert(userAccountId, medicine.id)

        //when:
        val actual = takingRecordDomainService.findOwnedTakingRecord(takingRecord.id, userAccountId)

        //then:
        assertThat(actual).usingRecursiveComparison().isEqualTo(takingRecord)
    }

    @Test
    @DisplayName("他のユーザーが記録した服用記録の場合、服用記録の取得に失敗する")
    fun takingRecordRecordedByAnotherUser_gettingTakingRecordFails() {
        //given:
        val anotherUserAccountId = testAccountInserter.insertAccountAndProfile().first.id
        val takingRecord = testTakingRecordInserter.insert(anotherUserAccountId, medicine.id)

        //when:
        val actual = takingRecordDomainService.findOwnedTakingRecord(takingRecord.id, userAccountId)

        //then:
        assertThat(actual).isNull()
    }
}