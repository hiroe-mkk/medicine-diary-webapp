package example.infrastructure.query.takingrecord

import example.application.query.shared.type.*
import example.application.query.takingrecord.*
import example.application.query.user.*
import example.application.service.takingrecord.*
import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.account.profile.profileimage.*
import example.domain.model.medicine.*
import example.domain.model.medicine.medicineImage.*
import example.domain.model.takingrecord.*
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
internal class MyBatisTakingRecordQueryServiceTest(@Autowired private val takingRecordQueryService: TakingRecordQueryService,
                                                   @Autowired private val testTakingRecordInserter: TestTakingRecordInserter,
                                                   @Autowired private val testMedicineInserter: TestMedicineInserter,
                                                   @Autowired private val testAccountInserter: TestAccountInserter,
                                                   @Autowired private val sharedGroupInserter: TestSharedGroupInserter) {
    @Test
    @DisplayName("服用記録一覧を取得する")
    fun getTakingRecords() {
        //given:
        val requesterProfile = testAccountInserter.insertAccountAndProfile().second
        val userSession = UserSessionFactory.create(requesterProfile.accountId)
        val requesterMedicine = testMedicineInserter.insert(MedicineOwner.create(requesterProfile.accountId))

        val (_, member) = testAccountInserter.insertAccountAndProfile()
        val sharedGroup = sharedGroupInserter.insert(members = setOf(requesterProfile.accountId, member.accountId))
        val sharedGroupMedicine = testMedicineInserter.insert(MedicineOwner.create(sharedGroup.id))

        val requesterTakingRecord = testTakingRecordInserter.insert(requesterProfile.accountId, sharedGroupMedicine.id)
        val memberTakingRecord = testTakingRecordInserter.insert(member.accountId, sharedGroupMedicine.id)
        val filter = TakingRecordFilter(sharedGroupMedicine.id,
                                        setOf(member.accountId),
                                        null, null)

        //when:
        val actualPage1 = takingRecordQueryService.findTakingRecordsPage(userSession,
                                                                         filter,
                                                                         PageRequest.of(0, 3))
        val actualPage2 = takingRecordQueryService.findTakingRecordsPage(userSession,
                                                                         filter,
                                                                         PageRequest.of(1, 3))

        //then:
        assertThat(actualPage1.totalPages).isEqualTo(1)
        assertThat(actualPage1.content.size).isEqualTo(1)
        assertThat(actualPage2.content.size).isEqualTo(0)
        val expectedDisplayTakingRecord =
                createDisplayTakingRecord(member, sharedGroupMedicine, memberTakingRecord, requesterProfile.accountId)
        assertThat(actualPage1.content[0]).isEqualTo(expectedDisplayTakingRecord)

    }

    fun createDisplayTakingRecord(profile: Profile,
                                  medicine: Medicine,
                                  takingRecord: TakingRecord,
                                  requester: AccountId): JSONTakingRecord {
        return JSONTakingRecord(takingRecord.id.value,
                                JSONTakenMedicine(medicine.id.value,
                                                  medicine.medicineName.value,
                                                  takingRecord.dose.toString() + medicine.dosageAndAdministration.takingUnit),
                                JSONFollowUp(takingRecord.followUp.symptom,
                                             takingRecord.followUp.beforeTaking.name,
                                             takingRecord.followUp.afterTaking?.name),
                                takingRecord.note.value,
                                DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm").format(takingRecord.takenAt),
                                JSONUser(profile.accountId.value,
                                         profile.username.value,
                                         profile.profileImageURL?.toURL()),
                                profile.accountId == requester)
    }
}