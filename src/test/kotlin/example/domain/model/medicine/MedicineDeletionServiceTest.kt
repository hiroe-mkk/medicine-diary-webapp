package example.domain.model.medicine

import example.application.service.medicine.*
import example.application.shared.usersession.*
import example.domain.model.medicine.medicineImage.*
import example.domain.model.sharedgroup.*
import example.domain.model.takingrecord.*
import example.domain.shared.type.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import io.mockk.*
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@MyBatisRepositoryTest
internal class MedicineDeletionServiceTest(@Autowired private val medicineRepository: MedicineRepository,
                                           @Autowired private val takingRecordRepository: TakingRecordRepository,
                                           @Autowired private val sharedGroupRepository: SharedGroupRepository,
                                           @Autowired private val testAccountInserter: TestAccountInserter,
                                           @Autowired private val testMedicineInserter: TestMedicineInserter,
                                           @Autowired private val testTakingRecordInserter: TestTakingRecordInserter) {
    private val medicineImageStorage: MedicineImageStorage = mockk(relaxed = true)
    private val medicineQueryService: MedicineQueryService =
            MedicineQueryService(medicineRepository, sharedGroupRepository)
    private val medicineDeletionService: MedicineDeletionService =
            MedicineDeletionService(medicineRepository,
                                    medicineImageStorage,
                                    takingRecordRepository,
                                    medicineQueryService)

    @Test
    @DisplayName("薬を削除する")
    fun deleteMedicine() {
        //given:
        val requesterAccountId = testAccountInserter.insertAccountAndProfile().first.id
        val medicineImageURL = MedicineImageURL("endpoint", "/medicineimage/oldMedicineImage")
        val medicine = testMedicineInserter.insert(MedicineOwner.create(requesterAccountId),
                                                   medicineImageURL = medicineImageURL)
        val takingRecord = testTakingRecordInserter.insert(requesterAccountId, medicine.id)

        //when:
        medicineDeletionService.deleteOne(medicine.id, requesterAccountId)

        //then:
        val foundMedicine = medicineRepository.findById(medicine.id)
        Assertions.assertThat(foundMedicine).isNull()
        val foundTakingRecord = takingRecordRepository.findById(takingRecord.id)
        Assertions.assertThat(foundTakingRecord).isNull()
        verify(exactly = 1) { medicineImageStorage.delete(medicineImageURL) }
    }
}