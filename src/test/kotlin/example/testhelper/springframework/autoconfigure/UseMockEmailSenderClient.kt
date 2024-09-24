package example.testhelper.springframework.autoconfigure

import example.application.service.sharedgroup.*
import example.domain.model.contact.*
import example.infrastructure.email.contact.*
import example.infrastructure.email.shared.*
import example.infrastructure.email.sharedgroup.*
import example.infrastructure.shared.*
import io.mockk.*
import org.springframework.boot.context.properties.*
import org.springframework.context.annotation.*

/**
 * モックされた EmailSenderClient の AutoConfiguration を有効にするアノテーション
 */
@Import(UseMockEmailSenderClient.Configuration::class)
@EnableConfigurationProperties(ApplicationProperties::class)
annotation class UseMockEmailSenderClient {
    class Configuration {
        @Bean
        fun emailSenderClient(): EmailSenderClient = mockk(relaxed = true)

        @Bean
        fun contactEmailSender(applicationProperties: ApplicationProperties,
                               emailSenderClient: EmailSenderClient): ContactEmailSender {
            return ContactEmailSenderImpl(applicationProperties, emailSenderClient)
        }

        @Bean
        fun sharedGroupInviteEmailSender(applicationProperties: ApplicationProperties,
                                         emailSenderClient: EmailSenderClient): SharedGroupInviteEmailSender {
            return SharedGroupInviteEmailSenderImpl(applicationProperties, emailSenderClient)
        }
    }
}
