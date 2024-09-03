package example.testhelper.springframework.autoconfigure

import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import example.domain.model.sharedgroup.*
import example.infrastructure.db.repository.account.*
import example.infrastructure.db.repository.medicationrecord.*
import example.infrastructure.db.repository.medicine.*
import example.infrastructure.db.repository.profile.*
import example.infrastructure.db.repository.sharedgroup.*
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
    class Configuration {
        @Bean
        fun accountRepository(accountMapper: AccountMapper): AccountRepository = MyBatisAccountRepository(accountMapper)

        @Bean
        fun profileRepository(profileMapper: ProfileMapper): ProfileRepository = MyBatisProfileRepository(profileMapper)

        @Bean
        fun medicineRepository(medicineMapper: MedicineMapper): MedicineRepository {
            return MyBatisMedicineRepository(medicineMapper)
        }

        @Bean
        fun medicationRecordRepository(medicationRecordMapper: MedicationRecordMapper): MedicationRecordRepository {
            return MyBatisMedicationRecordRepository(medicationRecordMapper)
        }

        @Bean
        fun sharedGroupRepository(sharedGroupMapper: SharedGroupMapper): SharedGroupRepository {
            return MyBatisSharedGroupRepository(sharedGroupMapper)
        }
    }
}
