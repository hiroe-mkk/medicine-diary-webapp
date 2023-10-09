package example.infrastructure.repository.takingrecord

import example.domain.model.account.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import example.domain.shared.type.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import java.time.*

@MyBatisRepositoryTest
internal class MyBatisTakingRecordRepositoryTest(@Autowired private val takingRecordRepository: TakingRecordRepository,
                                                 @Autowired private val testTakingRecordInserter: TestTakingRecordInserter,
                                                 @Autowired private val testMedicineInserter: TestMedicineInserter,
                                                 @Autowired private val testAccountInserter: TestAccountInserter) {
    private lateinit var accountId: AccountId
    private lateinit var medicineId: MedicineId

    @BeforeEach
    internal fun setUp() {
        val (account, _) = testAccountInserter.insertAccountAndProfile()
        accountId = account.id
        medicineId = testMedicineInserter.insert(accountId).id
    }

    @Test
    fun afterSavingTakingRecord_canFindById() {
        //given:
        val takingRecord = TakingRecord(TakingRecordId("testTakingRecordId"),
                                        accountId,
                                        medicineId,
                                        Dose(1.0),
                                        Symptoms(listOf(FollowUp("頭痛",
                                                                 ConditionLevel.A_LITTLE_BAD,
                                                                 ConditionLevel.GOOD))),
                                        Note("それほど酷い頭痛ではなかったけれど、早めに飲んでおいたらいつもより早めに治った気がする。"),
                                        LocalDateTime.of(2020, 1, 1, 7, 0))

        //when:
        takingRecordRepository.save(takingRecord)
        val foundTakingRecord = takingRecordRepository.findById(takingRecord.id)

        //then:
        assertThat(foundTakingRecord).usingRecursiveComparison().isEqualTo(takingRecord)
    }

    @Test
    fun canUpdateTakingRecord() {
        //given:
        val takingRecord = testTakingRecordInserter.insert(accountId, medicineId)
        takingRecord.modify(testMedicineInserter.insert(accountId).id,
                            Dose(2.0),
                            Symptoms(listOf(FollowUp("頭痛",
                                                     ConditionLevel.A_LITTLE_BAD,
                                                     null))),
                            Note(""))

        //when:
        takingRecordRepository.save(takingRecord)

        //then:
        val foundTakingRecord = takingRecordRepository.findById(takingRecord.id)
        assertThat(foundTakingRecord).usingRecursiveComparison().isEqualTo(takingRecord)
    }

    @Test
    fun canDeleteTakingRecord() {
        //given:
        val takingRecord = testTakingRecordInserter.insert(accountId, medicineId)

        //when:
        takingRecordRepository.delete(takingRecord.id)

        //then:
        val foundTakingRecord = takingRecordRepository.findById(takingRecord.id)
        assertThat(foundTakingRecord).isNull()
    }
}