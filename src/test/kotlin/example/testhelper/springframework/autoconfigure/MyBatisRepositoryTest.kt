package example.testhelper.springframework.autoconfigure

import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import example.domain.model.sharedgroup.*
import example.infrastructure.repository.account.*
import example.infrastructure.repository.medicationrecord.*
import example.infrastructure.repository.medicine.*
import example.infrastructure.repository.profile.*
import example.infrastructure.repository.sharedgroup.*
import org.mybatis.spring.boot.test.autoconfigure.*
import org.springframework.boot.test.autoconfigure.jdbc.*
import org.springframework.context.annotation.*

/**
 * MyBatis に関連する構成のみを適用したリポジトリのテストで利用可能なアノテーション
 */
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 組み込みデータベースへの置き換えを無効化する
@Import(MyBatisRepositoryTest.Configuration::class, TestDataInserterAutoConfiguration::class)
annotation class MyBatisRepositoryTest {
    class Configuration(private val accountMapper: AccountMapper,
                        private val profileMapper: ProfileMapper,
                        private val medicineMapper: MedicineMapper,
                        private val medicationRecordMapper: MedicationRecordMapper,
                        private val sharedGroupMapper: SharedGroupMapper) {
        @Bean
        fun accountRepository(): AccountRepository = MyBatisAccountRepository(accountMapper)

        @Bean
        fun profileRepository(): ProfileRepository = MyBatisProfileRepository(profileMapper)

        @Bean
        fun medicineRepository(): MedicineRepository = MyBatisMedicineRepository(medicineMapper)

        @Bean
        fun medicationRecordRepository(): MedicationRecordRepository {
            return MyBatisMedicationRecordRepository(medicationRecordMapper)
        }

        @Bean
        fun sharedGroupRepository(): SharedGroupRepository = MyBatisSharedGroupRepository(sharedGroupMapper)
    }
}