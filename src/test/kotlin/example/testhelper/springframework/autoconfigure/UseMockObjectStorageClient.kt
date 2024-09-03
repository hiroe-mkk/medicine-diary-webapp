package example.testhelper.springframework.autoconfigure

import example.infrastructure.objectstorage.shared.*
import io.mockk.*
import org.springframework.context.annotation.*

/**
 * モックされた ObjectStorageClient の AutoConfiguration を有効にするアノテーション
 */
@Import(UseMockObjectStorageClient.Configuration::class)
annotation class UseMockObjectStorageClient {
    class Configuration {
        @Bean
        fun objectStorageClient(): ObjectStorageClient {
            val mockObjectStorageClient: ObjectStorageClient = mockk(relaxed = true)
            every { mockObjectStorageClient.getEndpoint() } returns "endpoint"
            return mockObjectStorageClient
        }
    }
}
