package example.infrastructure.storage.shared.objectstrage.minio

import example.infrastructure.storage.shared.objectstrage.s3.*
import io.minio.*
import org.springframework.boot.context.properties.*
import org.springframework.context.annotation.*

@Profile("local", "dev")
@Configuration
@EnableConfigurationProperties(MinioProperties::class)
class MinioObjectStorageConfiguration(private val minioProperties: MinioProperties) {
    @Bean
    fun minioClient(): MinioClient {
        return MinioClient.builder()
            .endpoint(minioProperties.endpoint)
            .credentials(minioProperties.user, minioProperties.password)
            .build()
    }
}