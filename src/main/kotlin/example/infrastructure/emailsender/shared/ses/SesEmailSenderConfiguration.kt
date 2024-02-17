package example.infrastructure.emailsender.shared.ses

import com.amazonaws.services.simpleemail.*
import org.springframework.boot.context.properties.*
import org.springframework.context.annotation.*

@Profile("prod")
@Configuration
@EnableConfigurationProperties(SesProperties::class)
class SesEmailSenderConfiguration {
    @Bean
    fun amazonSimpleEmailService(): AmazonSimpleEmailService = AmazonSimpleEmailServiceClientBuilder.defaultClient()
}