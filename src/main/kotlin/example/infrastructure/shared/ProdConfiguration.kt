package example.infrastructure.shared

import com.amazonaws.services.s3.*
import com.amazonaws.services.simpleemail.*
import example.infrastructure.storage.shared.objectstrage.s3.*
import org.springframework.boot.context.properties.*
import org.springframework.context.annotation.*

@Profile("prod")
@Configuration
@EnableConfigurationProperties(S3Properties::class, ApplicationProperties::class)
class ProdConfiguration {
    @Bean
    fun amazonS3(): AmazonS3 = AmazonS3ClientBuilder.defaultClient()

    @Bean
    fun amazonSimpleEmailService(): AmazonSimpleEmailService = AmazonSimpleEmailServiceClientBuilder.defaultClient()
}
