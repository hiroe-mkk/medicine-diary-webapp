package example.application.service.medicine

import example.domain.model.medicine.*
import example.domain.shared.type.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@DomainLayerTest
class MedicineRegistrationServiceTest(@Autowired private val medicineRepository: MedicineRepository,
                                      @Autowired private val localDateTimeProvider: LocalDateTimeProvider,
                                      @Autowired private val medicineOwnerFactory: MedicineOwnerFactory,
                                      @Autowired private val testAccountInserter: TestAccountInserter) {
    private val medicineRegistrationService = MedicineRegistrationService(medicineRepository,
                                                                          localDateTimeProvider,
                                                                          medicineOwnerFactory)

    @Test
    @DisplayName("薬を登録する")
    fun registerMedicine() {
        //given:
        val userSession = UserSessionFactory.create(testAccountInserter.insertAccountAndProfile().first.id)
        val command = TestMedicineFactory.createCompletedRegistrationMedicineBasicInfoEditCommand()

        //when:
        val medicineId = medicineRegistrationService.registerMedicine(command, userSession)

        //then:
        val foundMedicine = medicineRepository.findById(medicineId)
        val expected = Medicine(medicineId,
                                MedicineOwner.create(userSession.accountId),
                                command.validatedMedicineName,
                                command.validatedDosageAndAdministration,
                                command.validatedEffects,
                                command.validatedPrecautions,
                                null,
                                command.isPublic,
                                null,
                                localDateTimeProvider.now())
        assertThat(foundMedicine).usingRecursiveComparison().isEqualTo(expected)
    }
}
