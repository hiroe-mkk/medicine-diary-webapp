package example.testhelper.springframework.autoconfigure

import example.infrastructure.emailsender.shared.*
import example.infrastructure.storage.shared.objectstrage.*
import io.mockk.*
import org.springframework.beans.factory.annotation.*
import org.springframework.context.annotation.*
import org.springframework.mail.javamail.*

/**
 * モックされた EmailSenderClient の AutoConfiguration を有効にするアノテーション
 */
@Import(UseMockEmailSenderClient.Configuration::class)
annotation class UseMockEmailSenderClient {
    class Configuration {
        @Bean
        fun emailSenderClient(): EmailSenderClient = mockk(relaxed = true)
    }
}