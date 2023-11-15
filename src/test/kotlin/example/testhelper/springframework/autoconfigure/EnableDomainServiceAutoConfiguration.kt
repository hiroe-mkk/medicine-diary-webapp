package example.testhelper.springframework.autoconfigure

import example.application.service.sharedgroup.*
import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.medicine.*
import example.domain.model.medicine.medicineImage.*
import example.domain.model.sharedgroup.*
import example.domain.model.takingrecord.*
import example.domain.shared.type.*
import example.infrastructure.storage.medicineimage.*
import example.infrastructure.storage.shared.objectstrage.*
import io.mockk.*
import org.springframework.context.annotation.*

/**
 * ドメインサービスの AutoConfiguration を有効にするアノテーション
 */
@Import(EnableDomainServiceAutoConfiguration.Configuration::class)
annotation class EnableDomainServiceAutoConfiguration {
    class Configuration(private val profileRepository: ProfileRepository,
                        private val medicineRepository: MedicineRepository,
                        private val takingRecordRepository: TakingRecordRepository,
                        private val sharedGroupRepository: SharedGroupRepository,
                        private val medicineImageStorage: MedicineImageStorage) {
        @Bean
        fun sharedGroupQueryService(): SharedGroupQueryService = SharedGroupQueryService(sharedGroupRepository)

        @Bean
        fun sharedGroupParticipationService(): SharedGroupParticipationService {
            return SharedGroupParticipationService(sharedGroupRepository, profileRepository)
        }

        @Bean
        fun sharedGroupUnshareService(): SharedGroupUnshareService {
            return SharedGroupUnshareService(sharedGroupRepository,
                                             sharedGroupQueryService(),
                                             medicineAndTakingRecordsDeletionService())
        }

        @Bean
        fun medicineQueryService(): MedicineQueryService {
            return MedicineQueryService(medicineRepository, sharedGroupRepository)
        }

        @Bean
        fun medicineCreationService(): MedicineCreationService {
            return MedicineCreationService(sharedGroupRepository)
        }

        @Bean
        fun medicineAndTakingRecordsDeletionService(): MedicineAndTakingRecordsDeletionService {
            return MedicineAndTakingRecordsDeletionService(medicineRepository,
                                                           medicineImageStorage,
                                                           takingRecordRepository,
                                                           medicineQueryService())
        }

        @Bean
        fun takingRecordQueryService(): TakingRecordQueryService {
            return TakingRecordQueryService(takingRecordRepository)
        }
    }
}