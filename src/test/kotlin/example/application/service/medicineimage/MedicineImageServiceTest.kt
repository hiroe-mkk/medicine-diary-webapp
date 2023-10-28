package example.application.service.medicineimage

import example.application.service.medicine.*
import example.application.shared.usersession.*
import example.domain.model.medicine.*
import example.domain.model.medicine.medicineImage.*
import example.domain.shared.type.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import io.mockk.*
import io.mockk.impl.annotations.*
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@MyBatisRepositoryTest
internal class MedicineImageServiceTest(@Autowired private val medicineRepository: MedicineRepository,
                                        @Autowired private val testAccountInserter: TestAccountInserter,
                                        @Autowired private val testMedicineInserter: TestMedicineInserter) {
    @MockK(relaxed = true)
    private lateinit var medicineImageStorage: MedicineImageStorage

    @InjectMockKs
    private lateinit var medicineImageService: MedicineImageService

    private lateinit var userSession: UserSession

    private val command = TestImageFactory.createImageUploadCommand()

    @BeforeEach
    internal fun setUp() {
        val (account, _) = testAccountInserter.insertAccountAndProfile()
        userSession = UserSessionFactory.create(account.id)
        every {
            medicineImageStorage.createURL()
        } returns MedicineImageURL("endpoint", "/medicineimage/newMedicineImage")
    }

    @Test
    @DisplayName("薬画像が未設定の場合、薬画像の変更に成功する")
    fun medicineImageIsNotSet_changingMedicineImageSucceeds() {
        //given:
        val medicine = testMedicineInserter.insert(userSession.accountId)

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
        val medicine = testMedicineInserter.insert(userSession.accountId,
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

    @Test
    @DisplayName("ユーザーが所有していない薬の場合、薬画像の変更に失敗する")
    fun medicineIsNotOwnedByUser_changingMedicineImageFails() {
        //given:
        val (anotherAccount, _) = testAccountInserter.insertAccountAndProfile()
        val medicine = testMedicineInserter.insert(anotherAccount.id)

        //when:
        val target: () -> Unit = {
            medicineImageService.changeMedicineImage(medicine.id, command, userSession)
        }

        //then:
        val medicineNotFoundException = assertThrows<MedicineNotFoundException>(target)
        assertThat(medicineNotFoundException.medicineId).isEqualTo(medicine.id)
    }
}