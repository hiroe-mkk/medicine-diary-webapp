package example.domain.model.medicine

import example.domain.model.account.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.medicineimage.*
import example.domain.shared.type.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@DomainLayerTest
internal class MedicineBasicInfoUpdateServiceTest(@Autowired private val medicineBasicInfoUpdateService: MedicineBasicInfoUpdateService,
                                                  @Autowired private val medicineRepository: MedicineRepository,
                                                  @Autowired private val medicationRecordRepository: MedicationRecordRepository,
                                                  @Autowired private val testAccountInserter: TestAccountInserter,
                                                  @Autowired private val testMedicineInserter: TestMedicineInserter,
                                                  @Autowired private val testMedicationRecordInserter: TestMedicationRecordInserter,
                                                  @Autowired private val testSharedGroupInserter: TestSharedGroupInserter) {
    private lateinit var requesterAccountId: AccountId
    private val newMedicineName: MedicineName = MedicineName("ロキソニンSプレミアム")
    private val newDosageAndAdministration: DosageAndAdministration = DosageAndAdministration(Dose(2.0),
                                                                                              "錠",
                                                                                              3,
                                                                                              listOf(Timing.AS_NEEDED))
    private val newEffects: Effects = Effects(listOf("頭痛", "解熱", "肩こり"))
    private val newPrecautions: Note = Note("服用間隔は4時間以上開けること。\n" +
                                            "再度症状があらわれた場合には3回目を服用してもよい。")
    private val newIsPublic = false

    @BeforeEach
    internal fun setUp() {
        requesterAccountId = testAccountInserter.insertAccountAndProfile().first.id
    }

    @Test
    @DisplayName("薬の所有者をアカウントから共有グループに変更する")
    fun changeMedicineOwnerFromAccountToSharedGroup() {
        //given:
        val sharedGroup = testSharedGroupInserter.insert(members = setOf(requesterAccountId))
        val medicine = testMedicineInserter.insert(MedicineOwner.create(requesterAccountId))
        val medicationRecord = testMedicationRecordInserter.insert(requesterAccountId, medicine.id)

        //when:
        medicineBasicInfoUpdateService.update(medicine.id,
                                              newMedicineName,
                                              newDosageAndAdministration,
                                              newEffects,
                                              newPrecautions,
                                              true,
                                              newIsPublic,
                                              requesterAccountId)

        //then:
        val foundMedicine = medicineRepository.findById(medicine.id)!!
        assertThat(foundMedicine)
            .usingRecursiveComparison()
            .isEqualTo(Medicine(medicine.id,
                                MedicineOwner.create(sharedGroup.id),
                                newMedicineName,
                                newDosageAndAdministration,
                                newEffects,
                                newPrecautions,
                                medicine.medicineImageURL,
                                true,
                                medicine.inventory,
                                medicine.registeredAt))
        assertThat(medicationRecord.takenMedicine).isEqualTo(medicine.id)
    }

    @Test
    @DisplayName("薬の所有者を共有グループからアカウントに変更する")
    fun changeMedicineOwnerFromSharedGroupToAccount() {
        //given
        val sharedGroup = testSharedGroupInserter.insert(members = setOf(requesterAccountId))
        val medicine = testMedicineInserter.insert(MedicineOwner.create(sharedGroup.id))
        val medicationRecord = testMedicationRecordInserter.insert(requesterAccountId, medicine.id)

        //when:
        medicineBasicInfoUpdateService.update(medicine.id,
                                              newMedicineName,
                                              newDosageAndAdministration,
                                              newEffects,
                                              newPrecautions,
                                              false,
                                              newIsPublic,
                                              requesterAccountId)

        //then:
        val foundMedicine = medicineRepository.findById(medicine.id)!!
        assertThat(foundMedicine)
            .usingRecursiveComparison()
            .isEqualTo(Medicine(medicine.id,
                                MedicineOwner.create(requesterAccountId),
                                newMedicineName,
                                newDosageAndAdministration,
                                newEffects,
                                newPrecautions,
                                medicine.medicineImageURL,
                                newIsPublic,
                                medicine.inventory,
                                medicine.registeredAt))
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

        //when:
        medicineBasicInfoUpdateService.update(medicine.id,
                                              newMedicineName,
                                              newDosageAndAdministration,
                                              newEffects,
                                              newPrecautions,
                                              false,
                                              newIsPublic,
                                              requesterAccountId)

        //then:
        val foundMedicationRecord1 = medicationRecordRepository.findById(medicationRecord1.id)!!
        assertThat(foundMedicationRecord1.takenMedicine).isEqualTo(medicine.id)
        verifyMedicineUpdated(medicine, foundMedicationRecord1)

        val foundMedicationRecord2 = medicationRecordRepository.findById(medicationRecord2.id)!!
        assertThat(foundMedicationRecord2.takenMedicine).isEqualTo(medicine.id)
        verifyMedicineUpdated(medicine, foundMedicationRecord2)

        val foundMedicationRecord3 = medicationRecordRepository.findById(medicationRecord3.id)!!
        assertThat(foundMedicationRecord3.takenMedicine).isNotEqualTo(medicine.id)
        verifyMedicineUpdated(medicine, foundMedicationRecord3)
    }

    fun verifyMedicineUpdated(original: Medicine, medicationRecord: MedicationRecord) {
        val foundMedicine = medicineRepository.findById(medicationRecord.takenMedicine)!!
        assertThat(foundMedicine)
            .usingRecursiveComparison()
            .isEqualTo(Medicine(medicationRecord.takenMedicine,
                                MedicineOwner.create(medicationRecord.recorder),
                                newMedicineName,
                                newDosageAndAdministration,
                                newEffects,
                                newPrecautions,
                                original.medicineImageURL,
                                newIsPublic,
                                original.inventory,
                                original.registeredAt))
    }
}