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
    class Configuration(private val JSONTakingRecordMapper: JSONTakingRecordMapper,
                        private val JSONSharedGroupMapper: JSONSharedGroupMapper,
                        private val JSONUserMapper: JSONUserMapper,
                        private val medicineRepository: MedicineRepository,
                        private val sharedGroupRepository: SharedGroupRepository) {
        @Bean
        fun takingRecordQueryService(): JSONTakingRecordQueryService {
            val medicineQueryService = MedicineQueryService(medicineRepository, sharedGroupRepository)
            return MyBatisJSONTakingRecordQueryService(JSONTakingRecordMapper, medicineQueryService)
        }

        @Bean
        fun sharedGroupQueryService(): JSONSharedGroupQueryService = MyBatisJSONSharedGroupQueryService(JSONSharedGroupMapper)

        @Bean
        fun userQueryService(): JSONUserQueryService = MyBatisJSONUserQueryService(JSONUserMapper)
    }
}