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
    class Configuration {
        @Bean
        fun sharedGroupQueryService(sharedGroupRepository: SharedGroupRepository): SharedGroupQueryService =
                SharedGroupQueryService(sharedGroupRepository)

        @Bean
        fun sharedGroupParticipationService(sharedGroupQueryService: SharedGroupQueryService,
                                            profileRepository: ProfileRepository): SharedGroupParticipationService {
            return SharedGroupParticipationService(sharedGroupQueryService, profileRepository)
        }

        @Bean
        fun sharedGroupUnshareService(sharedGroupRepository: SharedGroupRepository,
                                      medicineRepository: MedicineRepository,
                                      medicineImageStorage: MedicineImageStorage,
                                      medicationRecordRepository: MedicationRecordRepository,
                                      medicineQueryService: MedicineQueryService,
                                      medicineDeletionService: MedicineDeletionService,
                                      sharedGroupQueryService: SharedGroupQueryService,
                                      medicineOwnerCreationService: MedicineOwnerCreationService): SharedGroupUnshareService {
            return SharedGroupUnshareService(sharedGroupRepository,
                                             medicineRepository,
                                             medicineImageStorage,
                                             medicationRecordRepository,
                                             sharedGroupQueryService,
                                             medicineQueryService,
                                             medicineOwnerCreationService,
                                             medicineDeletionService)
        }

        @Bean
        fun medicineQueryService(medicineRepository: MedicineRepository,
                                 sharedGroupRepository: SharedGroupRepository): MedicineQueryService {
            return MedicineQueryService(medicineRepository, sharedGroupRepository)
        }

        @Bean
        fun medicineOwnerCreationService(sharedGroupQueryService: SharedGroupQueryService): MedicineOwnerCreationService {
            return MedicineOwnerCreationService(sharedGroupQueryService)
        }

        @Bean
        fun medicineCreationService(medicineOwnerCreationService: MedicineOwnerCreationService): MedicineCreationService {
            return MedicineCreationService(medicineOwnerCreationService)
        }

        @Bean
        fun medicineBasicInfoUpdateService(medicineRepository: MedicineRepository,
                                           medicationRecordRepository: MedicationRecordRepository,
                                           medicineImageStorage: MedicineImageStorage,
                                           medicineQueryService: MedicineQueryService,
                                           medicineOwnerCreationService: MedicineOwnerCreationService): MedicineBasicInfoUpdateService {
            return MedicineBasicInfoUpdateService(medicineRepository,
                                                  medicationRecordRepository,
                                                  medicineImageStorage,
                                                  medicineOwnerCreationService,
                                                  medicineQueryService)
        }

        @Bean
        fun medicineDeletionService(medicineRepository: MedicineRepository,
                                    medicationRecordRepository: MedicationRecordRepository,
                                    medicineImageStorage: MedicineImageStorage,
                                    medicineQueryService: MedicineQueryService): MedicineDeletionService {
            return MedicineDeletionService(medicineRepository,
                                           medicineImageStorage,
                                           medicationRecordRepository,
                                           medicineQueryService)
        }

        @Bean
        fun medicationRecordQueryService(medicationRecordRepository: MedicationRecordRepository): MedicationRecordQueryService {
            return MedicationRecordQueryService(medicationRecordRepository)
        }
    }
}