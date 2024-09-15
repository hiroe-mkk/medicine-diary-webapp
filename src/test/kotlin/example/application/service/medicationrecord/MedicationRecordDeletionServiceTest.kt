package example.application.service.medicationrecord

import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@DomainLayerTest
class MedicationRecordDeletionServiceTest(@Autowired private val medicationRecordRepository: MedicationRecordRepository,
                                          @Autowired private val medicationRecordFinder: MedicationRecordFinder,
                                          @Autowired private val testAccountInserter: TestAccountInserter,
                                          @Autowired private val testMedicineInserter: TestMedicineInserter,
                                          @Autowired private val testMedicationRecordInserter: TestMedicationRecordInserter) {
    private val medicationRecordDeletionService = MedicationRecordDeletionService(medicationRecordRepository,
                                                                                  medicationRecordFinder)

    @Test
    @DisplayName("服用記録を削除する")
    fun deleteMedicationRecord() {
        //given:
        val userSession = UserSessionFactory.create(testAccountInserter.insertAccountAndProfile().first.id)
        val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))
        val medicationRecord = testMedicationRecordInserter.insert(userSession.accountId, medicine.id)

        //when:
        medicationRecordDeletionService.deleteMedicationRecord(medicationRecord.id, userSession)

        //then:
        val foundMedicationRecord = medicationRecordRepository.findById(medicationRecord.id)
        assertThat(foundMedicationRecord).isNull()
    }
}
