package example.infrastructure.storage.shared.objectstrage.minio


import io.minio.*
import org.springframework.context.annotation.*
import org.springframework.stereotype.*

@Profile("local")
@Component
class MinioClientProvider(private val minioProperties: MinioProperties) {
    private val minioClient: MinioClient = create()

    fun getMinioClient(): MinioClient {
        return minioClient
    }

    private fun create(): MinioClient {
        return MinioClient.builder()
            .endpoint(minioProperties.endpoint)
            .credentials(minioProperties.user, minioProperties.password)
            .build()
    }
}