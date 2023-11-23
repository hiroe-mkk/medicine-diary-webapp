package example.testhelper.springframework.autoconfigure

import example.application.query.medicationrecord.*
import example.application.query.sharedgroup.*
import example.application.query.user.*
import example.domain.model.medicine.*
import example.domain.model.sharedgroup.*
import example.infrastructure.query.medicationrecord.*
import example.infrastructure.query.sharedgroup.*
import example.infrastructure.query.user.*
import org.springframework.context.annotation.*

/**
 * MyBatis に関連する構成のみを適用したクエリサービスのテストで利用可能なアノテーション
 */
@MyBatisRepositoryTest
@Import(MyBatisQueryServiceTest.Configuration::class)
annotation class MyBatisQueryServiceTest {
    class Configuration(private val jsonMedicationRecordMapper: JSONMedicationRecordMapper,
                        private val jsonSharedGroupMapper: JSONSharedGroupMapper,
                        private val jsonUserMapper: JSONUserMapper,
                        private val medicineRepository: MedicineRepository,
                        private val sharedGroupRepository: SharedGroupRepository) {
        @Bean
        fun medicationRecordQueryService(): JSONMedicationRecordQueryService {
            val medicineQueryService = MedicineQueryService(medicineRepository, sharedGroupRepository)
            val sharedGroupQueryService = SharedGroupQueryService(sharedGroupRepository)
            return MyBatisJSONMedicationRecordQueryService(jsonMedicationRecordMapper,
                                                           medicineQueryService,
                                                           sharedGroupQueryService)
        }

        @Bean
        fun sharedGroupQueryService(): JSONSharedGroupQueryService =
                MyBatisJSONSharedGroupQueryService(jsonSharedGroupMapper)

        @Bean
        fun jsonUserQueryService(): JSONUserQueryService = MyBatisJSONUserQueryService(jsonUserMapper)
    }
}