package example.infrastructure.objectstorage.shared.minio

import org.springframework.boot.context.properties.*
import org.springframework.context.annotation.*

@Profile("dev-local", "dev-container")
@ConfigurationProperties(prefix = "infrastructure.storage.minio")
data class MinioProperties(val user: String,
                           val password: String,
                           val endpoint: String,
                           val bucketName: String)
