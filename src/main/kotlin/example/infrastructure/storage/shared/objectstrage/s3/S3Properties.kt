package example.infrastructure.storage.shared.objectstrage.s3

import org.springframework.boot.context.properties.*
import org.springframework.context.annotation.*

@Profile("prod")
@ConfigurationProperties(prefix = "aws")
data class S3Properties(val accessKey: String,
                        val secretKey: String,
                        val region: String,
                        val s3RootUrl: String,
                        val bucketName: String) {
    val rootPath: String = "$s3RootUrl/$bucketName"
}