package example.testhelper.springframework.autoconfigure

import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import example.testhelper.inserter.*
import org.springframework.context.annotation.*
import org.springframework.stereotype.*

/**
 * モックされた TestDataInserter の AutoConfiguration を有効にするアノテーション
 */
@Import(EnableTestDataInserter.Configuration::class)
annotation class EnableTestDataInserter {
    class Configuration(private val accountRepository: AccountRepository,
                        private val profileRepository: ProfileRepository,
                        private val medicineRepository: MedicineRepository,
                        private val takingRecordRepository: TakingRecordRepository) {
        @Bean
        fun testAccountInserter(): TestAccountInserter = TestAccountInserter(accountRepository, profileRepository)

        @Bean
        fun testMedicineInserter(): TestMedicineInserter = TestMedicineInserter(medicineRepository)

        @Bean
        fun testTakingRecordInserter(): TestTakingRecordInserter = TestTakingRecordInserter(takingRecordRepository)
    }
}