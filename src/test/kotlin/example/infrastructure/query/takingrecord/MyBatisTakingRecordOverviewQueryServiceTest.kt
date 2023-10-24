package example.infrastructure.query.takingrecord

import example.application.query.shared.type.*
import example.application.query.takingrecord.*
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

@MyBatisRepositoryTest
internal class MyBatisTakingRecordOverviewQueryServiceTest(@Autowired private val takingRecordOverviewQueryService: TakingRecordOverviewQueryService,
                                                           @Autowired private val testTakingRecordInserter: TestTakingRecordInserter,
                                                           @Autowired private val testMedicineInserter: TestMedicineInserter,
                                                           @Autowired private val testAccountInserter: TestAccountInserter) {
    @Test
    @DisplayName("薬IDをもとに服用記録概要一覧を取得する")
    fun getTakingRecordDetailsByMedicineId() {
        //given:
        val (account, profile) = testAccountInserter.insertAccountAndProfile(
                profileImageURL = ProfileImageURL("endpoint", "/path"))
        val userSession = UserSessionFactory.create(account.id)
        val medicine = testMedicineInserter.insert(account.id)

        val localDateTime = LocalDateTime.of(2020, 1, 1, 0, 0)
        val takingRecord = List(5) { index ->
            testTakingRecordInserter.insert(account.id,
                                            medicine.id,
                                            takenAt = localDateTime.plusDays(index.toLong()))
        }

        //when:
        val actualPage1 = takingRecordOverviewQueryService.findTakingRecordDetailsByMedicineId(medicine.id,
                                                                                               userSession,
                                                                                               PageRequest.of(0, 3))
        val actualPage2 = takingRecordOverviewQueryService.findTakingRecordDetailsByMedicineId(medicine.id,
                                                                                               userSession,
                                                                                               PageRequest.of(1, 3))

        //then:
        assertThat(actualPage1.totalPages).isEqualTo(2)
        assertThat(actualPage1.content.size).isEqualTo(3)
        assertThat(actualPage2.content.size).isEqualTo(2)
        val expectedPage1 = arrayOf(createTakingRecordOverview(profile, medicine, takingRecord[4]),
                                    createTakingRecordOverview(profile, medicine, takingRecord[3]),
                                    createTakingRecordOverview(profile, medicine, takingRecord[2]))
        assertThat(actualPage1.content).containsExactly(*expectedPage1)
        val expectedPage2 = arrayOf(createTakingRecordOverview(profile, medicine, takingRecord[1]),
                                    createTakingRecordOverview(profile, medicine, takingRecord[0]))
        assertThat(actualPage2.content).containsExactly(*expectedPage2)
    }

    fun createTakingRecordOverview(profile: Profile,
                                   medicine: Medicine,
                                   takingRecord: TakingRecord): TakingRecordOverview {
        return TakingRecordOverview(takingRecord.id,
                                    takingRecord.followUp.beforeTaking,
                                    takingRecord.followUp.afterTaking,
                                    takingRecord.takenAt,
                                    medicine.id,
                                    medicine.name,
                                    User(profile.accountId,
                                         profile.username,
                                         profile.profileImageURL))
    }
}