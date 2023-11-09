package example.testhelper.springframework.autoconfigure

import example.application.query.sharedgroup.*
import example.application.query.takingrecord.*
import example.application.query.user.*
import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.medicine.*
import example.domain.model.sharedgroup.*
import example.domain.model.takingrecord.*
import example.infrastructure.query.sharedgroup.*
import example.infrastructure.query.takingrecord.*
import example.infrastructure.query.user.*
import example.infrastructure.repository.account.*
import example.infrastructure.repository.medicine.*
import example.infrastructure.repository.profile.*
import example.infrastructure.repository.sharedgroup.*
import example.infrastructure.repository.takingrecord.*
import org.mybatis.spring.boot.test.autoconfigure.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.test.autoconfigure.jdbc.*
import org.springframework.context.annotation.*

/**
 * MyBatis に関連する構成のみを適用したリポジトリのテストで利用可能なアノテーション
 */
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 組み込みデータベースへの置き換えを無効化する
@EnableTestDataInserter
@Import(MyBatisRepositoryTest.Configuration::class)
annotation class MyBatisRepositoryTest {
    class Configuration(private val accountMapper: AccountMapper,
                        private val profileMapper: ProfileMapper,
                        private val medicineMapper: MedicineMapper,
                        private val takingRecordMapper: TakingRecordMapper,
                        private val sharedGroupMapper: SharedGroupMapper) {
        @Bean
        fun accountRepository(): AccountRepository = MyBatisAccountRepository(accountMapper)

        @Bean
        fun profileRepository(): ProfileRepository = MyBatisProfileRepository(profileMapper)

        @Bean
        fun medicineRepository(): MedicineRepository = MyBatisMedicineRepository(medicineMapper)

        @Bean
        fun takingRecordRepository(): TakingRecordRepository = MyBatisTakingRecordRepository(takingRecordMapper)

        @Bean
        fun sharedGroupRepository(): SharedGroupRepository = MyBatisSharedGroupRepository(sharedGroupMapper)
    }
}