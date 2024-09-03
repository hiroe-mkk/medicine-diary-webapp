package example.testhelper.springframework.autoconfigure

import example.domain.model.account.profile.profileimage.*
import example.domain.model.medicine.medicineimage.*
import example.infrastructure.objectstorage.medicineimage.*
import example.infrastructure.objectstorage.profileimage.*
import example.infrastructure.objectstorage.shared.*
import org.springframework.context.annotation.*

/**
 * オブジェクトストレージの AutoConfiguration を有効にするアノテーション
 */
@Import(EnableObjectStorageAutoConfiguration.Configuration::class)
annotation class EnableObjectStorageAutoConfiguration {
    class Configuration {
        @Bean
        fun medicineImageStorage(objectStorageClient: ObjectStorageClient): MedicineImageStorage {
            return ObjectStorageMedicineImageStorage(objectStorageClient)
        }

        @Bean
        fun profileImageStorage(objectStorageClient: ObjectStorageClient): ProfileImageStorage {
            return ObjectStorageProfileImageStorage(objectStorageClient)
        }
    }
}
