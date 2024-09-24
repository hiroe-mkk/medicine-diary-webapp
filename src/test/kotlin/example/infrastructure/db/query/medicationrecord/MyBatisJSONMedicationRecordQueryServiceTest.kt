package example.infrastructure.db.query.medicationrecord

import example.application.query.medicationrecord.*
import example.application.query.user.*
import example.application.shared.usersession.*
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
import java.time.*
import java.time.format.*

@MyBatisQueryServiceTest
internal class MyBatisJSONMedicationRecordQueryServiceTest(@Autowired private val jsonMedicationRecordQueryService: JSONMedicationRecordQueryService,
                                                           @Autowired private val testMedicationRecordInserter: TestMedicationRecordInserter,
                                                           @Autowired private val testMedicineInserter: TestMedicineInserter,
                                                           @Autowired private val testAccountInserter: TestAccountInserter,
                                                           @Autowired private val testSharedGroupInserter: TestSharedGroupInserter) {
    private lateinit var userSession: UserSession
    private lateinit var requester: Profile
    private lateinit var member: Profile

    private lateinit var requesterPublicMedicine: Medicine
    private lateinit var requesterPrivateMedicine: Medicine
    private lateinit var memberPublicMedicine: Medicine
    private lateinit var memberPrivateMedicine: Medicine
    private lateinit var sharedGroupMedicine: Medicine

    private lateinit var requesterPublicMedicineRecord: MedicationRecord
    private lateinit var requesterPrivateMedicineRecord: MedicationRecord
    private lateinit var memberPublicMedicineRecord: MedicationRecord
    private lateinit var memberPrivateMedicineRecord: MedicationRecord
    private lateinit var requesterSharedGroupMedicineRecord: MedicationRecord

    private val date = LocalDate.of(2020, 1, 1)

    @BeforeEach
    internal fun setUp() {
        requester = testAccountInserter.insertAccountAndProfile().second
        userSession = UserSessionFactory.create(requester.accountId)
        member = testAccountInserter.insertAccountAndProfile().second
        val sharedGroupId = testSharedGroupInserter.insert(members = setOf(userSession.accountId, member.accountId)).id

        val requesterMedicineOwner = MedicineOwner.create(userSession.accountId)
        requesterPublicMedicine = testMedicineInserter.insert(owner = requesterMedicineOwner, isPublic = true)
        requesterPublicMedicineRecord = testMedicationRecordInserter.insert(userSession.accountId,
                                                                            requesterPublicMedicine.id,
                                                                            takenMedicineOn = date)
        requesterPrivateMedicine = testMedicineInserter.insert(owner = requesterMedicineOwner, isPublic = false)
        requesterPrivateMedicineRecord = testMedicationRecordInserter.insert(userSession.accountId,
                                                                             requesterPrivateMedicine.id,
                                                                             takenMedicineOn = date.plusDays(1))

        val memberMedicineOwner = MedicineOwner.create(member.accountId)
        memberPublicMedicine = testMedicineInserter.insert(owner = memberMedicineOwner, isPublic = true)
        memberPublicMedicineRecord = testMedicationRecordInserter.insert(member.accountId,
                                                                         memberPublicMedicine.id,
                                                                         takenMedicineOn = date.plusDays(2))
        memberPrivateMedicine = testMedicineInserter.insert(owner = memberMedicineOwner, isPublic = false)
        memberPrivateMedicineRecord = testMedicationRecordInserter.insert(member.accountId,
                                                                          memberPrivateMedicine.id,
                                                                          takenMedicineOn = date.plusDays(3))

        val sharedGroupMedicineOwner = MedicineOwner.create(sharedGroupId)
        sharedGroupMedicine = testMedicineInserter.insert(owner = sharedGroupMedicineOwner, isPublic = true)
        requesterSharedGroupMedicineRecord = testMedicationRecordInserter.insert(userSession.accountId,
                                                                                 sharedGroupMedicine.id,
                                                                                 takenMedicineOn = date.plusDays(4))
    }

    @Test
    @DisplayName("服用記録一覧を取得する")
    fun getMedicationRecords() {
        //given:
        val filter = MedicationRecordFilter(null, null, null, null)

        //when:
        val actualPage1 = jsonMedicationRecordQueryService.getMedicationRecordsPage(filter,
                                                                                    PageRequest.of(0, 5),
                                                                                    userSession)
        val actualPage2 = jsonMedicationRecordQueryService.getMedicationRecordsPage(filter,
                                                                                    PageRequest.of(1, 5),
                                                                                    userSession)

        //then:
        assertThat(actualPage1.totalPages).isEqualTo(1)
        assertThat(actualPage1.number).isEqualTo(0)
        val expectedRequesterPublicMedicationRecord = createJSONMedicationRecord(requester,
                                                                                 requesterPrivateMedicine,
                                                                                 requesterPrivateMedicineRecord,
                                                                                 userSession.accountId)
        val expectedRequesterPrivateMedicationRecord = createJSONMedicationRecord(requester,
                                                                                  requesterPublicMedicine,
                                                                                  requesterPublicMedicineRecord,
                                                                                  userSession.accountId)
        val expectedMemberPublicMedicationRecord = createJSONMedicationRecord(member,
                                                                              memberPublicMedicine,
                                                                              memberPublicMedicineRecord,
                                                                              userSession.accountId)
        val expectedRequesterSharedGroupMedicineRecord = createJSONMedicationRecord(requester,
                                                                                    sharedGroupMedicine,
                                                                                    requesterSharedGroupMedicineRecord,
                                                                                    userSession.accountId)
        assertThat(actualPage1.medicationRecords)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(expectedRequesterPublicMedicationRecord,
                                       expectedRequesterPrivateMedicationRecord,
                                       expectedMemberPublicMedicationRecord,
                                       expectedRequesterSharedGroupMedicineRecord)
        assertThat(actualPage2.number).isEqualTo(1)
        assertThat(actualPage2.medicationRecords).isEmpty()
    }


    @Test
    @DisplayName("アカウントでフィルタリングされた服用記録一覧を取得する")
    fun getMedicationRecordsByAccount() {
        //given:
        val filter = MedicationRecordFilter(null, requester.accountId.value, null, null)

        //when:
        val actual = jsonMedicationRecordQueryService.getMedicationRecordsPage(filter,
                                                                               PageRequest.of(0, 5),
                                                                               userSession)

        //then:
        assertThat(actual.totalPages).isEqualTo(1)
        assertThat(actual.medicationRecords)
            .extracting("medicationRecordId")
            .containsExactlyInAnyOrder(requesterPublicMedicineRecord.id.value,
                                       requesterPrivateMedicineRecord.id.value,
                                       requesterSharedGroupMedicineRecord.id.value)
    }

    @Test
    @DisplayName("薬でフィルタリングされた服用記録一覧を取得する")
    fun getMedicationRecordsByMedicine() {
        //given:
        val filter = MedicationRecordFilter(requesterPublicMedicine.id.value, null, null, null)

        //when:
        val actual = jsonMedicationRecordQueryService.getMedicationRecordsPage(filter,
                                                                               PageRequest.of(0, 5),
                                                                               userSession)

        //then:
        assertThat(actual.totalPages).isEqualTo(1)
        assertThat(actual.medicationRecords)
            .extracting("medicationRecordId")
            .containsExactlyInAnyOrder(requesterPublicMedicineRecord.id.value)
    }

    @Test
    @DisplayName("日付でフィルタリングされた服用記録一覧を取得する")
    fun getMedicationRecordsByDate() {
        //given:
        val filter = MedicationRecordFilter(null, null, date, date.plusDays(2))

        //when:
        val actual = jsonMedicationRecordQueryService.getMedicationRecordsPage(filter,
                                                                               PageRequest.of(0, 5),
                                                                               userSession)

        //then:
        assertThat(actual.totalPages).isEqualTo(1)
        assertThat(actual.medicationRecords)
            .extracting("medicationRecordId")
            .containsExactlyInAnyOrder(requesterPublicMedicineRecord.id.value,
                                       requesterPrivateMedicineRecord.id.value,
                                       memberPublicMedicineRecord.id.value)
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
