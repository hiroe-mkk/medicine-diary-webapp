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
import org.springframework.data.domain.*
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
        val takingRecord = TakingRecord.reconstruct(TakingRecordId("testTakingRecordId"),
                                                    accountId,
                                                    medicineId,
                                                    Dose(1.0),
                                                    Symptoms(listOf(FollowUp("頭痛",
                                                                             ConditionLevel.A_LITTLE_BAD,
                                                                             null))),
                                                    Note(""),
                                                    LocalDateTime.of(2020, 1, 1, 7, 0))

        //when:
        takingRecordRepository.save(takingRecord)
        val foundTakingRecord = takingRecordRepository.findById(takingRecord.id)

        //then:
        assertThat(foundTakingRecord).usingRecursiveComparison().isEqualTo(takingRecord)
    }

    @Test
    fun canFindAllByMedicineId() {
        //given:
        val localDateTime = LocalDateTime.of(2020, 1, 1, 0, 0)
        val takingRecord1 = testTakingRecordInserter.insert(accountId, medicineId, takenAt = localDateTime)
        val takingRecord2 = testTakingRecordInserter.insert(accountId, medicineId, takenAt = localDateTime.plusDays(1))
        val takingRecord3 = testTakingRecordInserter.insert(accountId, medicineId, takenAt = localDateTime.plusDays(2))
        val takingRecord4 = testTakingRecordInserter.insert(accountId, medicineId, takenAt = localDateTime.plusDays(3))
        val takingRecord5 = testTakingRecordInserter.insert(accountId, medicineId, takenAt = localDateTime.plusDays(4))

        //when:
        val actualPage1 = takingRecordRepository.findByTakenMedicine(medicineId, PageRequest.of(0, 3))
        val actualPage2 = takingRecordRepository.findByTakenMedicine(medicineId, PageRequest.of(1, 3))

        //then:
        assertThat(actualPage1.totalPages).isEqualTo(2)
        assertThat(actualPage1.content.size).isEqualTo(3)
        assertThat(actualPage2.content.size).isEqualTo(2)
        val expectedPage1 = arrayOf(takingRecord5.id, takingRecord4.id, takingRecord3.id)
        assertThat(actualPage1.content.map { it.id }).containsExactly(*expectedPage1)
        val expectedPage2 = arrayOf(takingRecord2.id, takingRecord1.id)
        assertThat(actualPage2.content.map { it.id }).containsExactly(*expectedPage2)
    }

    @Test
    fun canUpdateTakingRecord() {
        //given:
        val takingRecord = testTakingRecordInserter.insert(accountId, medicineId)
        takingRecord.modify(testMedicineInserter.insert(accountId),
                            Dose(2.0),
                            Symptoms(listOf(FollowUp("頭痛",
                                                     ConditionLevel.A_LITTLE_BAD,
                                                     ConditionLevel.GOOD))),
                            Note("それほど酷い頭痛ではなかったけれど、早めに飲んでおいたらいつもより早めに治った気がする。"))

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