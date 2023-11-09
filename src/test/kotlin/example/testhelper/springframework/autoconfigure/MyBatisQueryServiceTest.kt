package example.testhelper.springframework.autoconfigure

import example.application.query.sharedgroup.*
import example.application.query.takingrecord.*
import example.application.query.user.*
import example.domain.model.medicine.*
import example.domain.model.sharedgroup.*
import example.infrastructure.query.sharedgroup.*
import example.infrastructure.query.takingrecord.*
import example.infrastructure.query.user.*
import org.springframework.context.annotation.*

/**
 * MyBatis に関連する構成のみを適用したクエリサービスのテストで利用可能なアノテーション
 */
@MyBatisRepositoryTest
@Import(MyBatisQueryServiceTest.Configuration::class)
annotation class MyBatisQueryServiceTest {
    class Configuration(private val takingRecordOverviewMapper: TakingRecordOverviewMapper,
                        private val takingRecordDetailMapper: TakingRecordDetailMapper,
                        private val sharedGroupDetailMapper: SharedGroupDetailMapper,
                        private val userMapper: UserMapper,
                        private val medicineRepository: MedicineRepository,
                        private val sharedGroupRepository: SharedGroupRepository) {
        @Bean
        fun takingRecordQueryService(): TakingRecordQueryService {
            val medicineDomainService = MedicineDomainService(medicineRepository, sharedGroupRepository)
            return MyBatisTakingRecordQueryService(takingRecordOverviewMapper,
                                                   takingRecordDetailMapper,
                                                   medicineDomainService)
        }

        @Bean
        fun sharedGroupQueryService(): SharedGroupQueryService = MyBatisSharedGroupQueryService(sharedGroupDetailMapper)

        @Bean
        fun userQueryService(): UserQueryService = MyBatisUserQueryService(userMapper)
    }
}