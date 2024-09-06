package example.testhelper.springframework.autoconfigure

import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import example.domain.model.medicine.medicineimage.*
import example.domain.model.sharedgroup.*
import example.domain.shared.type.*
import io.mockk.*
import org.springframework.context.annotation.*
import java.time.*

/**
 * ドメインサービスの AutoConfiguration を有効にするアノテーション
 */
@Import(EnableDomainServiceAutoConfiguration.Configuration::class)
annotation class EnableDomainServiceAutoConfiguration {
    class Configuration {
        @Bean
        fun sharedGroupLeaveService(sharedGroupRepository: SharedGroupRepository,
                                    medicineRepository: MedicineRepository,
                                    medicineImageStorage: MedicineImageStorage,
                                    medicationRecordRepository: MedicationRecordRepository,
                                    medicineQueryService: MedicineQueryService,
                                    medicineDeletionService: MedicineDeletionService,
                                    medicineOwnerCreationService: MedicineOwnerCreationService): SharedGroupLeaveService {
            return SharedGroupLeaveService(sharedGroupRepository,
                                           medicineRepository,
                                           medicineImageStorage,
                                           medicationRecordRepository,
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
        fun medicineOwnerCreationService(sharedGroupRepository: SharedGroupRepository): MedicineOwnerCreationService {
            return MedicineOwnerCreationService(sharedGroupRepository)
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

        @Bean
        fun localDateTimeProvider(): LocalDateTimeProvider {
            val localDateTimeProvider: LocalDateTimeProvider = mockk();
            every { localDateTimeProvider.now() } returns LocalDateTime.of(2020, 1, 1, 0, 0)
            every { localDateTimeProvider.today() } returns LocalDate.of(2020, 1, 1)
            return localDateTimeProvider
        }
    }
}
