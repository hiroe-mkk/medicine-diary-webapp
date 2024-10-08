package example.infrastructure.shared

import example.infrastructure.objectstorage.shared.minio.MinioProperties
import io.minio.*
import org.springframework.boot.context.properties.*
import org.springframework.context.annotation.*

@Profile("dev-local", "dev-container")
@Configuration
@EnableConfigurationProperties(MinioProperties::class, ApplicationProperties::class)
class DevConfiguration {
    @Bean
    fun minioClient(minioProperties: MinioProperties): MinioClient {
        return MinioClient.builder()
            .endpoint(minioProperties.endpoint)
            .credentials(minioProperties.user, minioProperties.password)
            .build()
    }
}
