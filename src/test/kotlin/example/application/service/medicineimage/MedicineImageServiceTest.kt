package example.application.service.medicineimage

import example.application.service.medicine.*
import example.application.shared.usersession.*
import example.domain.model.medicine.*
import example.domain.model.medicine.medicineImage.*
import example.domain.model.sharedgroup.*
import example.domain.shared.type.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import io.mockk.*
import io.mockk.impl.annotations.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@DomainLayerTest
internal class MedicineImageServiceTest(@Autowired private val medicineRepository: MedicineRepository,
                                        @Autowired private val medicineImageStorage: MedicineImageStorage,
                                        @Autowired private val medicineQueryService: MedicineQueryService,
                                        @Autowired private val testAccountInserter: TestAccountInserter,
                                        @Autowired private val testMedicineInserter: TestMedicineInserter) {
    private val medicineImageService: MedicineImageService = MedicineImageService(medicineRepository,
                                                                                  medicineImageStorage,
                                                                                  medicineQueryService)

    private lateinit var userSession: UserSession

    private val command = TestImageFactory.createImageUploadCommand()

    @BeforeEach
    internal fun setUp() {
        val requesterAccountId = testAccountInserter.insertAccountAndProfile().first.id
        userSession = UserSessionFactory.create(requesterAccountId)
    }

    @Test
    @DisplayName("薬画像が未設定の場合、薬画像の変更に成功する")
    fun medicineImageIsNotSet_changingMedicineImageSucceeds() {
        //given:
        val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))

        //when:
        val newMedicineImageURL = medicineImageService.changeMedicineImage(medicine.id,
                                                                           command,
                                                                           userSession)

        //then:
        val foundMedicine = medicineRepository.findById(medicine.id)!!
        assertThat(foundMedicine.medicineImageURL).isEqualTo(newMedicineImageURL)
        verify(exactly = 0) { medicineImageStorage.delete(any()) }
        verify(exactly = 1) { medicineImageStorage.upload(newMedicineImageURL, any()) }
    }

    @Test
    @DisplayName("薬画像が設定済みの場合、薬画像の変更に成功する")
    fun medicineImageIsSet_changingMedicineImageSucceeds() {
        //given:
        val oldMedicineImageURL = MedicineImageURL("endpoint", "/medicineimage/oldMedicineImage")
        val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId),
                                                   medicineImageURL = oldMedicineImageURL)

        //when:
        val newMedicineImageURL = medicineImageService.changeMedicineImage(medicine.id,
                                                                           command,
                                                                           userSession)

        //then:
        val foundMedicine = medicineRepository.findById(medicine.id)!!
        assertThat(foundMedicine.medicineImageURL).isEqualTo(newMedicineImageURL)
        verify(exactly = 1) { medicineImageStorage.upload(newMedicineImageURL, any()) }
        verify(exactly = 1) { medicineImageStorage.delete(oldMedicineImageURL) }
    }

    @Test
    @DisplayName("薬が見つからなかった場合、薬画像の変更に失敗する")
    fun medicineNotFound_changingMedicineImageFails() {
        //given:
        val badMedicineId = MedicineId("NonexistentId")

        //when:
        val target: () -> Unit = {
            medicineImageService.changeMedicineImage(badMedicineId, command, userSession)
        }

        //then:
        val medicineNotFoundException = assertThrows<MedicineNotFoundException>(target)
        assertThat(medicineNotFoundException.medicineId).isEqualTo(badMedicineId)
    }
}