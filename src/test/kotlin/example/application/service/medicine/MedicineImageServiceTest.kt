package example.application.service.medicine

import example.application.shared.usersession.*
import example.domain.model.medicine.*
import example.domain.model.medicine.medicineimage.*
import example.infrastructure.db.repository.shared.*
import example.infrastructure.objectstorage.shared.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import io.mockk.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@DomainLayerTest
class MedicineImageServiceTest(@Autowired private val medicineRepository: MedicineRepository,
                               @Autowired private val medicineImageStorage: MedicineImageStorage,
                               @Autowired private val objectStorageClient: ObjectStorageClient,
                               @Autowired private val medicineQueryService: MedicineQueryService,
                               @Autowired private val testAccountInserter: TestAccountInserter,
                               @Autowired private val testMedicineInserter: TestMedicineInserter) {
    private val medicineImageService = MedicineImageService(medicineRepository,
                                                            medicineImageStorage,
                                                            medicineQueryService)

    private lateinit var userSession: UserSession

    @BeforeEach
    internal fun setUp() {
        userSession = UserSessionFactory.create(testAccountInserter.insertAccountAndProfile().first.id)
        clearMocks(objectStorageClient)
    }

    @Nested
    inner class ChangeMedicineImage {
        private val command = TestImageFactory.createImageUploadCommand()

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
            verify(exactly = 0) { objectStorageClient.remove(any()) }
            verify(exactly = 1) { objectStorageClient.put(newMedicineImageURL, any()) }
        }

        @Test
        @DisplayName("薬画像が設定済みの場合、薬画像の変更に成功する")
        fun medicineImageIsSet_changingMedicineImageSucceeds() {
            //given:
            val oldMedicineImageURL = medicineImageStorage.createURL()
            val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId),
                                                       medicineImageURL = oldMedicineImageURL)

            //when:
            val newMedicineImageURL = medicineImageService.changeMedicineImage(medicine.id,
                                                                               command,
                                                                               userSession)

            //then:
            val foundMedicine = medicineRepository.findById(medicine.id)!!
            assertThat(foundMedicine.medicineImageURL).isEqualTo(newMedicineImageURL)
            verify(exactly = 1) { objectStorageClient.put(newMedicineImageURL, any()) }
            verify(exactly = 1) { objectStorageClient.remove(oldMedicineImageURL) }
        }

        @Test
        @DisplayName("薬が見つからなかった場合、薬画像の変更に失敗する")
        fun medicineNotFound_changingMedicineImageFails() {
            //given:
            val nonexistentMedicineId = MedicineId(EntityIdHelper.generate())

            //when:
            val target: () -> Unit = {
                medicineImageService.changeMedicineImage(nonexistentMedicineId, command, userSession)
            }

            //then:
            val medicineNotFoundException = assertThrows<MedicineNotFoundException>(target)
            assertThat(medicineNotFoundException.medicineId).isEqualTo(nonexistentMedicineId)
        }
    }

    @Test
    @DisplayName("薬画像を削除する")
    fun deleteMedicineImage() {
        //given:
        val medicineImageURL = medicineImageStorage.createURL()
        val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId),
                                                   medicineImageURL = medicineImageURL)

        //when:
        medicineImageService.deleteMedicineImage(medicine.id, userSession)

        //then:
        val foundMedicine = medicineRepository.findById(medicine.id)!!
        assertThat(foundMedicine.medicineImageURL).isNull()
        verify(exactly = 1) { objectStorageClient.remove(medicineImageURL) }
    }
}
