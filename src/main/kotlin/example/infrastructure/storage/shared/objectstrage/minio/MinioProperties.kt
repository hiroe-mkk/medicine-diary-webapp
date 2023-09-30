package example.infrastructure.storage.shared.objectstrage.minio

import org.springframework.boot.context.properties.*

@ConfigurationProperties(prefix = "minio")
class MinioProperties(val user: String,
                      val password: String,
                      val endpoint: String,
                      val bucketName: String) {
    val rootPath: String = "$endpoint/$bucketName"
}