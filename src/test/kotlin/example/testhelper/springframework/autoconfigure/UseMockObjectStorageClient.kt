package example.testhelper.springframework.autoconfigure

import example.infrastructure.storage.shared.objectstrage.*
import io.minio.*
import io.mockk.*
import org.codehaus.groovy.runtime.DefaultGroovyMethods.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.context.properties.*
import org.springframework.boot.test.context.*
import org.springframework.context.annotation.*
import org.springframework.test.context.junit.jupiter.*

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