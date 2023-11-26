package example.domain.model.medicine

import example.domain.model.account.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.medicineimage.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@DomainLayerTest
internal class MedicineOwnerChangeServiceTest(@Autowired private val medicineOwnerChangeService: MedicineOwnerChangeService,
                                              @Autowired private val medicineRepository: MedicineRepository,
                                              @Autowired private val medicationRecordRepository: MedicationRecordRepository,
                                              @Autowired private val testAccountInserter: TestAccountInserter,
                                              @Autowired private val testMedicineInserter: TestMedicineInserter,
                                              @Autowired private val testMedicationRecordInserter: TestMedicationRecordInserter,
                                              @Autowired private val testSharedGroupInserter: TestSharedGroupInserter) {
    private lateinit var requesterAccountId: AccountId

    @BeforeEach
    internal fun setUp() {
        requesterAccountId = testAccountInserter.insertAccountAndProfile().first.id
    }

    @Test
    @DisplayName("薬の所有者をアカウントから共有グループに変更する")
    fun changeMedicineOwnerFromAccountToSharedGroup() {
        //given:
        val sharedGroup = testSharedGroupInserter.insert(members = setOf(requesterAccountId))
        val medicineImageURL = MedicineImageURL("endpoint", "/medicineimage/oldMedicineImage")
        val medicine = testMedicineInserter.insert(MedicineOwner.create(requesterAccountId),
                                                   medicineImageURL = medicineImageURL)
        val medicationRecord = testMedicationRecordInserter.insert(requesterAccountId, medicine.id)
        val newOwner = MedicineOwner.create(sharedGroup.id)

        //when:
        medicineOwnerChangeService.changeOwner(medicine.id, newOwner, requesterAccountId)

        //then:
        val foundMedicine = medicineRepository.findById(medicine.id)!!
        assertThat(foundMedicine.owner).isEqualTo(newOwner)
        assertThat(medicationRecord.takenMedicine).isEqualTo(medicine.id)
    }

    @Test
    @DisplayName("薬の所有者を共有グループからアカウントに変更する")
    fun changeMedicineOwnerFromSharedGroupToAccount() {
        //given
        val sharedGroup = testSharedGroupInserter.insert(members = setOf(requesterAccountId))
        val medicine = testMedicineInserter.insert(MedicineOwner.create(sharedGroup.id))
        val medicationRecord = testMedicationRecordInserter.insert(requesterAccountId, medicine.id)
        val newOwner = MedicineOwner.create(requesterAccountId)

        //when:
        medicineOwnerChangeService.changeOwner(medicine.id, newOwner, requesterAccountId)

        //then:
        val foundMedicine = medicineRepository.findById(medicine.id)!!
        assertThat(foundMedicine.owner).isEqualTo(newOwner)
        assertThat(medicationRecord.takenMedicine).isEqualTo(medicine.id)
    }

    @Test
    @DisplayName("記録者が複数いる場合、記録者ごとに薬を複製する")
    fun multipleRecordersExist_cloneMedicineForIndividualRecorders() {
        //given
        val user1AccountId = testAccountInserter.insertAccountAndProfile().first.id
        val sharedGroupWithMultipleMembers = testSharedGroupInserter.insert(members = setOf(requesterAccountId,
                                                                                            user1AccountId))
        val medicine = testMedicineInserter.insert(MedicineOwner.create(sharedGroupWithMultipleMembers.id))
        val medicationRecord1 = testMedicationRecordInserter.insert(requesterAccountId, medicine.id)
        val medicationRecord2 = testMedicationRecordInserter.insert(requesterAccountId, medicine.id)
        val medicationRecord3 = testMedicationRecordInserter.insert(user1AccountId, medicine.id)
        val newOwner = MedicineOwner.create(requesterAccountId)

        //when:
        medicineOwnerChangeService.changeOwner(medicine.id, newOwner, requesterAccountId)

        //then:
        val foundMedicine = medicineRepository.findById(medicine.id)
        assertThat(foundMedicine).isNull()
        verifyTakenMedicineChanged(medicine, medicationRecord1)
        verifyTakenMedicineChanged(medicine, medicationRecord2)
        verifyTakenMedicineChanged(medicine, medicationRecord3)
    }

    fun verifyTakenMedicineChanged(original: Medicine, medicationRecord: MedicationRecord) {
        val foundMedicationRecord = medicationRecordRepository.findById(medicationRecord.id)!!
        val foundMedicine = medicineRepository.findById(foundMedicationRecord.takenMedicine)!!
        assertThat(foundMedicine).usingRecursiveComparison().ignoringFields("id", "owner").isEqualTo(original)
        assertThat(foundMedicine.owner.accountId).isEqualTo(foundMedicationRecord.recorder)
    }
}