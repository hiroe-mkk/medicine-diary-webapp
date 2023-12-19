package example.infrastructure.query.medicationrecord

import example.application.query.medicationrecord.*
import example.application.query.user.*
import example.application.service.medicationrecord.*
import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.account.profile.profileimage.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import example.domain.model.medicine.medicineimage.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.data.domain.*
import java.time.*
import java.time.format.*

@MyBatisQueryServiceTest
internal class MyBatisJSONMedicationRecordQueryServiceTest(@Autowired private val jsonMedicationRecordQueryService: JSONMedicationRecordQueryService,
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
        val filter = MedicationRecordFilter(sharedGroupMedicine.id,
                                            member.accountId,
                                            null, null)

        //when:
        val actualPage1 = jsonMedicationRecordQueryService.findJSONMedicationRecordsPage(userSession,
                                                                                         filter,
                                                                                         PageRequest.of(0, 3))
        val actualPage2 = jsonMedicationRecordQueryService.findJSONMedicationRecordsPage(userSession,
                                                                                         filter,
                                                                                         PageRequest.of(1, 3))

        //then:
        assertThat(actualPage1.totalPages).isEqualTo(1)
        assertThat(actualPage1.content.size).isEqualTo(1)
        assertThat(actualPage2.content.size).isEqualTo(0)
        val expectedDisplayMedicationRecord = createDisplayMedicationRecord(member,
                                                                            sharedGroupMedicine,
                                                                            memberMedicationRecord,
                                                                            requesterProfile.accountId)
        assertThat(actualPage1.content[0]).isEqualTo(expectedDisplayMedicationRecord)

    }

    fun createDisplayMedicationRecord(profile: Profile,
                                      medicine: Medicine,
                                      medicationRecord: MedicationRecord,
                                      requester: AccountId): JSONMedicationRecord {
        return JSONMedicationRecord(medicationRecord.id.value,
                                    JSONTakenMedicine(medicine.id.value,
                                                      medicine.medicineName.value,
                                                      medicationRecord.dose.toString() + medicine.dosageAndAdministration.doseUnit),
                                    JSONFollowUp(medicationRecord.followUp.symptom,
                                                 medicationRecord.followUp.beforeMedication.name,
                                                 medicationRecord.followUp.afterMedication?.name),
                                    medicationRecord.note.value,
                                    DateTimeFormatter.ofPattern("yyyy/MM/dd").format(medicationRecord.takenMedicineOn),
                                    DateTimeFormatter.ofPattern("HH:mm").format(medicationRecord.takenMedicineAt),
                                    JSONUser(profile.accountId.value,
                                             profile.username.value,
                                             profile.profileImageURL?.toURL()),
                                    profile.accountId == requester)
    }
}