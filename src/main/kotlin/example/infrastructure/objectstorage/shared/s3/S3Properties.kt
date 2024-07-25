package example.infrastructure.objectstorage.shared.s3

import org.springframework.boot.context.properties.*
import org.springframework.context.annotation.*

@Profile("prod")
@ConfigurationProperties(prefix = "infrastructure.storage.s3")
data class S3Properties(val endpoint: String,
                        val bucketName: String)
