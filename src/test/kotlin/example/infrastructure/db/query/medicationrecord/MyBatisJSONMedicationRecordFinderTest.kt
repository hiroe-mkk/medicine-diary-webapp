package example.infrastructure.db.query.medicationrecord

import example.application.query.medicationrecord.*
import example.application.query.user.*
import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.data.domain.*
import java.time.format.*

@MyBatisQueryServiceTest
internal class MyBatisJSONMedicationRecordFinderTest(@Autowired private val jsonMedicationRecordQueryService: JSONMedicationRecordQueryService,
                                                     @Autowired private val testMedicationRecordInserter: TestMedicationRecordInserter,
                                                     @Autowired private val testMedicineInserter: TestMedicineInserter,
                                                     @Autowired private val testAccountInserter: TestAccountInserter,
                                                     @Autowired private val sharedGroupInserter: TestSharedGroupInserter) {
    @Test
    @DisplayName("服用記録一覧を取得する")
    fun getMedicationRecords() {
        //given:
        val requesterProfile = testAccountInserter.insertAccountAndProfile().second
        val userSession = UserSessionFactory.create(requesterProfile.accountId)
        val requesterMedicine = testMedicineInserter.insert(MedicineOwner.create(requesterProfile.accountId))

        val (_, member) = testAccountInserter.insertAccountAndProfile()
        val sharedGroup = sharedGroupInserter.insert(members = setOf(requesterProfile.accountId, member.accountId))
        val sharedGroupMedicine = testMedicineInserter.insert(MedicineOwner.create(sharedGroup.id))

        val requesterMedicationRecord = testMedicationRecordInserter.insert(requesterProfile.accountId,
                                                                            sharedGroupMedicine.id)
        val memberMedicationRecord = testMedicationRecordInserter.insert(member.accountId, sharedGroupMedicine.id)
        val filter = MedicationRecordFilter(sharedGroupMedicine.id.toString(),
                                            member.accountId.toString(),
                                            null, null)

        //when:
        val actualPage1 = jsonMedicationRecordQueryService.getMedicationRecordsPage(filter,
                                                                                    PageRequest.of(0, 3),
                                                                                    userSession)
        val actualPage2 = jsonMedicationRecordQueryService.getMedicationRecordsPage(filter,
                                                                                    PageRequest.of(1, 3),
                                                                                    userSession)

        //then:
        assertThat(actualPage1.totalPages).isEqualTo(1)
        assertThat(actualPage1.number).isEqualTo(0)
        val expectedDisplayMedicationRecord = createJSONMedicationRecord(member,
                                                                         sharedGroupMedicine,
                                                                         memberMedicationRecord,
                                                                         requesterProfile.accountId)
        assertThat(actualPage1.medicationRecords)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(expectedDisplayMedicationRecord)
        assertThat(actualPage2.number).isEqualTo(1)
        assertThat(actualPage2.medicationRecords).isEmpty()
    }

    fun createJSONMedicationRecord(profile: Profile,
                                   medicine: Medicine,
                                   medicationRecord: MedicationRecord,
                                   requester: AccountId): JSONMedicationRecord {
        return JSONMedicationRecord(medicationRecord.id.value,
                                    JSONTakenMedicine(medicine.id.value,
                                                      medicine.medicineName.value,
                                                      medicationRecord.dose.toString() + medicine.dosageAndAdministration.doseUnit),
                                    JSONFollowUp(medicationRecord.followUp.symptom,
                                                 medicationRecord.followUp.beforeMedication,
                                                 medicationRecord.followUp.afterMedication),
                                    medicationRecord.note,
                                    DateTimeFormatter.ofPattern("yyyy/MM/dd").format(medicationRecord.takenMedicineOn),
                                    DateTimeFormatter.ofPattern("HH:mm").format(medicationRecord.takenMedicineAt),
                                    medicationRecord.symptomOnsetAt?.let {
                                        DateTimeFormatter.ofPattern("HH:mm").format(it)
                                    },
                                    medicationRecord.onsetEffectAt?.let {
                                        DateTimeFormatter.ofPattern("HH:mm").format(it)
                                    },
                                    JSONUser(profile.accountId.value,
                                             profile.username.value,
                                             profile.profileImageURL?.toURL()),
                                    profile.accountId == requester)
    }
}
