package example.testhelper.springframework.autoconfigure

import example.domain.model.account.profile.*
import example.domain.model.account.profile.profileimage.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import example.domain.model.medicine.medicineimage.*
import example.domain.model.sharedgroup.*
import example.infrastructure.storage.medicineimage.*
import example.infrastructure.storage.profileimage.*
import example.infrastructure.storage.shared.objectstrage.*
import org.springframework.context.annotation.*

/**
 * オブジェクトストレージの AutoConfiguration を有効にするアノテーション
 */
@Import(EnableObjectStorageAutoConfiguration.Configuration::class)
annotation class EnableObjectStorageAutoConfiguration {
    class Configuration(private val objectStorageClient: ObjectStorageClient) {
        @Bean
        fun medicineImageStorage(): MedicineImageStorage {
            return ObjectStorageMedicineImageStorage(objectStorageClient)
        }

        @Bean
        fun profileImageStorage(): ProfileImageStorage {
            return ObjectStorageProfileImageStorage(objectStorageClient)
        }
    }
}