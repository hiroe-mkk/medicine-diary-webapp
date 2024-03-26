package example.testhelper.springframework.autoconfigure

import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import example.domain.model.sharedgroup.*
import example.testhelper.inserter.*
import org.springframework.boot.test.context.*
import org.springframework.context.annotation.*

/**
 * TestDataInserter の AutoConfiguration を有効にするアノテーション
 */
@TestConfiguration
class TestDataInserterAutoConfiguration {
    @Bean
    fun testAccountInserter(accountRepository: AccountRepository,
                            profileRepository: ProfileRepository): TestAccountInserter {
        return TestAccountInserter(accountRepository, profileRepository)
    }

    @Bean
    fun testMedicineInserter(medicineRepository: MedicineRepository): TestMedicineInserter {
        return TestMedicineInserter(medicineRepository)
    }

    @Bean
    fun testMedicationRecordInserter(medicationRecordRepository: MedicationRecordRepository): TestMedicationRecordInserter {
        return TestMedicationRecordInserter(medicationRecordRepository)
    }

    @Bean
    fun testSharedGroupInserter(sharedGroupRepository: SharedGroupRepository): TestSharedGroupInserter {
        return TestSharedGroupInserter(sharedGroupRepository)
    }
}