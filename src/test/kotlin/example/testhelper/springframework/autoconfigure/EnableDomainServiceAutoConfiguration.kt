package example.testhelper.springframework.autoconfigure

import example.application.service.sharedgroup.*
import example.domain.model.account.profile.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import example.domain.model.medicine.medicineimage.*
import example.domain.model.sharedgroup.*
import example.domain.shared.type.*
import example.infrastructure.shared.*
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
                                    medicineFinder: MedicineFinder,
                                    medicineDeletionCoordinator: MedicineDeletionCoordinator,
                                    medicineOwnerCreationService: MedicineOwnerCreationService): SharedGroupLeaveService {
            return SharedGroupLeaveService(sharedGroupRepository,
                                           medicineRepository,
                                           medicineImageStorage,
                                           medicationRecordRepository,
                                           medicineFinder,
                                           medicineOwnerCreationService,
                                           medicineDeletionCoordinator)
        }

        @Bean
        fun sharedGroupInviteService(sharedGroupRepository: SharedGroupRepository,
                                     sharedGroupInviteEmailSender: SharedGroupInviteEmailSender,
                                     localDateTimeProvider: LocalDateTimeProvider,
                                     profileRepository: ProfileRepository,
                                     applicationProperties: ApplicationProperties): SharedGroupInviteService {
            return SharedGroupInviteService(sharedGroupRepository,
                                            sharedGroupInviteEmailSender,
                                            localDateTimeProvider,
                                            profileRepository,
                                            applicationProperties)
        }

        @Bean
        fun medicineFinder(medicineRepository: MedicineRepository,
                           sharedGroupRepository: SharedGroupRepository): MedicineFinder {
            return MedicineFinder(medicineRepository, sharedGroupRepository)
        }

        @Bean
        fun medicineCreationService(medicineOwnerCreationService: MedicineOwnerCreationService): MedicineCreationService {
            return MedicineCreationService(medicineOwnerCreationService)
        }

        @Bean
        fun medicineBasicInfoUpdateService(medicineRepository: MedicineRepository,
                                           medicationRecordRepository: MedicationRecordRepository,
                                           medicineImageStorage: MedicineImageStorage,
                                           medicineFinder: MedicineFinder,
                                           medicineOwnerCreationService: MedicineOwnerCreationService): MedicineBasicInfoUpdateService {
            return MedicineBasicInfoUpdateService(medicineRepository,
                                                  medicationRecordRepository,
                                                  medicineImageStorage,
                                                  medicineOwnerCreationService,
                                                  medicineFinder)
        }

        @Bean
        fun medicineOwnerCreationService(sharedGroupRepository: SharedGroupRepository): MedicineOwnerCreationService {
            return MedicineOwnerCreationService(sharedGroupRepository)
        }

        @Bean
        fun medicineDeletionCoordinator(medicineRepository: MedicineRepository,
                                        medicationRecordRepository: MedicationRecordRepository,
                                        medicineImageStorage: MedicineImageStorage,
                                        medicineFinder: MedicineFinder): MedicineDeletionCoordinator {
            return MedicineDeletionCoordinator(medicineRepository,
                                               medicineImageStorage,
                                               medicationRecordRepository,
                                               medicineFinder)
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
