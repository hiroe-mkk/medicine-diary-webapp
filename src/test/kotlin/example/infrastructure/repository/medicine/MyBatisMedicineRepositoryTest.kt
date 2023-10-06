package example.infrastructure.repository.medicine

import example.domain.model.account.*
import example.domain.model.medicine.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import java.time.*

@MyBatisRepositoryTest
internal class MyBatisMedicineRepositoryTest(@Autowired private val medicineRepository: MedicineRepository,
                                             @Autowired private val testMedicineInserter: TestMedicineInserter,
                                             @Autowired private val testAccountInserter: TestAccountInserter) {
    private lateinit var accountId: AccountId

    @BeforeEach
    internal fun setUp() {
        val (account, _) = testAccountInserter.insertAccountAndProfile()
        accountId = account.id
    }

    @Test
    fun afterSavingMedicine_canFindById() {
        //given:
        val medicine = Medicine(MedicineId("testMedicineId"),
                                accountId,
                                "ロキソニンS",
                                Dosage(1.0, "錠"),
                                Administration(3, emptyList()),
                                Effects(listOf("頭痛", "解熱", "肩こり")),
                                "服用間隔は4時間以上開けること。",
                                LocalDateTime.of(2020, 1, 1, 0, 0))

        //when:
        medicineRepository.save(medicine)
        val foundMedicine = medicineRepository.findById(medicine.id)

        //then:
        assertThat(foundMedicine).usingRecursiveComparison().isEqualTo(medicine)
    }

    @Test
    fun findAllMedicineByAccountId() {
        //given:
        val localDateTime = LocalDateTime.of(2020, 1, 1, 0, 0)
        val medicine1 = testMedicineInserter.insert(accountId, registeredAt = localDateTime)
        val medicine2 = testMedicineInserter.insert(accountId, registeredAt = localDateTime.plusDays(1))
        val medicine3 = testMedicineInserter.insert(accountId, registeredAt = localDateTime.plusDays(2))

        //when:
        val actual = medicineRepository.findByAccountId(accountId)

        //then:
        val expected = arrayOf(medicine3.id, medicine2.id, medicine1.id)
        assertThat(actual.map { it.id }).containsExactly(*expected)
    }

    @Test
    fun canDeleteMedicine() {
        //given:
        val medicine = testMedicineInserter.insert(accountId)

        //when:
        medicineRepository.delete(medicine.id)

        //then:
        val foundMedicine = medicineRepository.findById(medicine.id)
        assertThat(foundMedicine).isNull()
    }
}