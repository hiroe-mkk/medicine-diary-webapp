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
@Import(EnableMockObjectStorageClient.Configuration::class)
annotation class EnableMockObjectStorageClient {
    class Configuration {
        private val mockObjectStorageClient: ObjectStorageClient = mockk(relaxed = true)

        @Bean
        fun objectStorageClient(): ObjectStorageClient {
            every { mockObjectStorageClient.getRootPath() } returns "rootPath"
            return mockObjectStorageClient
        }
    }
}