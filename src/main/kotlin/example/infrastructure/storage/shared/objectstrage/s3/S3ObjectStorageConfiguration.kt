package example.infrastructure.storage.shared.objectstrage.s3

import com.amazonaws.services.s3.*
import example.infrastructure.storage.shared.objectstrage.minio.*
import example.infrastructure.storage.shared.objectstrage.s3.*
import org.springframework.boot.context.properties.*
import org.springframework.context.annotation.*

@Profile("prod")
@Configuration
@EnableConfigurationProperties(S3Properties::class)
class S3ObjectStorageConfiguration {
    @Bean
    fun amazonS3(): AmazonS3 = AmazonS3ClientBuilder.defaultClient()
}