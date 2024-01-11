package example.infrastructure.emailsender.shared

import org.springframework.boot.context.properties.*
import org.springframework.context.annotation.*

@Configuration
@EnableConfigurationProperties(ApplicationProperties::class)
class ApplicationPropertiesConfiguration