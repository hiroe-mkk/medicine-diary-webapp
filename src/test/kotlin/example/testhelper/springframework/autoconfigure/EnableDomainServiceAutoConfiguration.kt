package example.testhelper.springframework.autoconfigure

import example.domain.model.account.profile.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import example.domain.model.medicine.medicineimage.*
import example.domain.model.sharedgroup.*
import org.springframework.context.annotation.*

/**
 * ドメインサービスの AutoConfiguration を有効にするアノテーション
 */
@Import(EnableDomainServiceAutoConfiguration.Configuration::class)
annotation class EnableDomainServiceAutoConfiguration {
    class Configuration(private val profileRepository: ProfileRepository,
                        private val medicineRepository: MedicineRepository,
                        private val medicationRecordRepository: MedicationRecordRepository,
                        private val sharedGroupRepository: SharedGroupRepository,
                        private val medicineImageStorage: MedicineImageStorage) {
        @Bean
        fun sharedGroupQueryService(): SharedGroupQueryService = SharedGroupQueryService(sharedGroupRepository)

        @Bean
        fun sharedGroupParticipationService(): SharedGroupParticipationService {
            return SharedGroupParticipationService(sharedGroupQueryService(), profileRepository)
        }

        @Bean
        fun sharedGroupUnshareService(): SharedGroupUnshareService {
            return SharedGroupUnshareService(sharedGroupRepository,
                                             medicineRepository,
                                             medicineImageStorage,
                                             medicationRecordRepository,
                                             sharedGroupQueryService(),
                                             medicineQueryService(),
                                             medicineOwnerCreationService(),
                                             medicineDeletionService())
        }

        @Bean
        fun medicineQueryService(): MedicineQueryService {
            return MedicineQueryService(medicineRepository, sharedGroupRepository)
        }

        @Bean
        fun medicineOwnerCreationService(): MedicineOwnerCreationService {
            return MedicineOwnerCreationService(sharedGroupQueryService())
        }

        @Bean
        fun medicineCreationService(): MedicineCreationService {
            return MedicineCreationService(medicineOwnerCreationService())
        }

        @Bean
        fun medicineBasicInfoUpdateService(): MedicineBasicInfoUpdateService {
            return MedicineBasicInfoUpdateService(medicineRepository,
                                                  medicationRecordRepository,
                                                  medicineImageStorage,
                                                  medicineOwnerCreationService(),
                                                  medicineQueryService())
        }

        @Bean
        fun medicineDeletionService(): MedicineDeletionService {
            return MedicineDeletionService(medicineRepository,
                                           medicineImageStorage,
                                           medicationRecordRepository,
                                           medicineQueryService())
        }

        @Bean
        fun medicationRecordQueryService(): MedicationRecordQueryService {
            return MedicationRecordQueryService(medicationRecordRepository)
        }
    }
}