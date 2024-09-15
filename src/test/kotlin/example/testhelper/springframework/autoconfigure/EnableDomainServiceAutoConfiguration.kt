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
        fun sharedGroupFinder(sharedGroupRepository: SharedGroupRepository): SharedGroupFinder {
            return SharedGroupFinder(sharedGroupRepository)
        }

        @Bean
        fun sharedGroupLeaveCoordinator(sharedGroupRepository: SharedGroupRepository,
                                        medicineRepository: MedicineRepository,
                                        medicineImageStorage: MedicineImageStorage,
                                        medicationRecordRepository: MedicationRecordRepository,
                                        medicineFinder: MedicineFinder,
                                        medicineDeletionCoordinator: MedicineDeletionCoordinator,
                                        medicineOwnerFactory: MedicineOwnerFactory): SharedGroupLeaveCoordinator {
            return SharedGroupLeaveCoordinator(sharedGroupRepository,
                                               medicineRepository,
                                               medicineImageStorage,
                                               medicationRecordRepository,
                                               medicineFinder,
                                               medicineOwnerFactory,
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
        fun medicineBasicInfoUpdater(medicineRepository: MedicineRepository,
                                     medicationRecordRepository: MedicationRecordRepository,
                                     medicineImageStorage: MedicineImageStorage,
                                     medicineFinder: MedicineFinder,
                                     medicineOwnerFactory: MedicineOwnerFactory): MedicineBasicInfoUpdater {
            return MedicineBasicInfoUpdater(medicineRepository,
                                            medicationRecordRepository,
                                            medicineImageStorage,
                                            medicineOwnerFactory,
                                            medicineFinder)
        }

        @Bean
        fun medicineOwnerFactory(sharedGroupRepository: SharedGroupRepository): MedicineOwnerFactory {
            return MedicineOwnerFactory(sharedGroupRepository)
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
        fun medicationRecordFinder(medicationRecordRepository: MedicationRecordRepository): MedicationRecordFinder {
            return MedicationRecordFinder(medicationRecordRepository)
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
