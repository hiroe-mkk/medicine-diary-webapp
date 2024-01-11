package example.infrastructure.storage.shared.objectstrage.s3

import org.springframework.boot.context.properties.*
import org.springframework.context.annotation.*

@Profile("prod")
@ConfigurationProperties(prefix = "aws.s3")
data class S3Properties(val endpoint: String,
                        val bucketName: String)