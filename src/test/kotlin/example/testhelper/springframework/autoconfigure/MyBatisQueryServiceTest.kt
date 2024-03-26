package example.testhelper.springframework.autoconfigure

import example.application.query.medicationrecord.*
import example.application.query.medicine.*
import example.application.query.sharedgroup.*
import example.application.query.user.*
import example.domain.model.medicine.*
import example.domain.model.sharedgroup.*
import example.infrastructure.query.medicationrecord.*
import example.infrastructure.query.medicine.*
import example.infrastructure.query.sharedgroup.*
import example.infrastructure.query.user.*
import org.springframework.context.annotation.*

/**
 * MyBatis に関連する構成のみを適用したクエリサービスのテストで利用可能なアノテーション
 */
@MyBatisRepositoryTest
@Import(MyBatisQueryServiceTest.Configuration::class)
annotation class MyBatisQueryServiceTest {
    class Configuration {
        @Bean
        fun medicineOverviewsQueryService(jsonMedicineOverviewsMapper: JsonMedicineOverviewsMapper): JSONMedicineOverviewsQueryService {
            return MyBatisJSONMedicineOverviewsQueryService(jsonMedicineOverviewsMapper)
        }

        @Bean
        fun medicationRecordQueryService(jsonMedicationRecordMapper: JSONMedicationRecordMapper,
                                         medicineRepository: MedicineRepository,
                                         sharedGroupRepository: SharedGroupRepository): JSONMedicationRecordQueryService {
            val medicineQueryService = MedicineQueryService(medicineRepository, sharedGroupRepository)
            val sharedGroupQueryService = SharedGroupQueryService(sharedGroupRepository)
            return MyBatisJSONMedicationRecordQueryService(jsonMedicationRecordMapper,
                                                           medicineQueryService,
                                                           sharedGroupQueryService)
        }

        @Bean
        fun sharedGroupQueryService(jsonSharedGroupMapper: JSONSharedGroupMapper): JSONSharedGroupQueryService =
                MyBatisJSONSharedGroupQueryService(jsonSharedGroupMapper)

        @Bean
        fun userQueryService(userMapper: UserMapper): UserQueryService {
            return MyBatisUserQueryService(userMapper)
        }

        @Bean
        fun jsonUserQueryService(jsonUserMapper: JSONUserMapper): JSONUserQueryService {
            return MyBatisJSONUserQueryService(jsonUserMapper)
        }
    }
}