package example.testhelper.springframework.autoconfigure

import example.application.query.medicationrecord.*
import example.application.query.medicine.*
import example.application.query.user.*
import example.domain.model.medicine.*
import example.domain.model.sharedgroup.*
import example.infrastructure.db.query.medicationrecord.*
import example.infrastructure.db.query.medicine.*
import example.infrastructure.db.query.user.*
import org.springframework.context.annotation.*

/**
 * MyBatis に関連する構成のみを適用したクエリサービスのテストで利用可能なアノテーション
 */
@MyBatisRepositoryTest
@Import(MyBatisQueryServiceTest.Configuration::class)
annotation class MyBatisQueryServiceTest {
    class Configuration {
        @Bean
        fun medicineOverviewsQueryService(jsonMedicineOverviewsMapper: JSONMedicineOverviewsMapper): JSONMedicineOverviewsQueryService {
            return MyBatisJSONMedicineOverviewsQueryService(jsonMedicineOverviewsMapper)
        }

        @Bean
        fun medicationRecordQueryService(jsonMedicationRecordMapper: JSONMedicationRecordMapper,
                                         medicineRepository: MedicineRepository,
                                         sharedGroupRepository: SharedGroupRepository): JSONMedicationRecordQueryService {
            val medicineFinder = MedicineFinder(medicineRepository, sharedGroupRepository)
            return MyBatisJSONMedicationRecordQueryService(jsonMedicationRecordMapper,
                                                           sharedGroupRepository,
                                                           medicineFinder)
        }

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
