package example.infrastructure.emailsender.shared.ses

import org.springframework.boot.context.properties.*
import org.springframework.context.annotation.*

@Profile("prod")
@ConfigurationProperties(prefix = "aws.ses")
class SesProperties(val address: String)