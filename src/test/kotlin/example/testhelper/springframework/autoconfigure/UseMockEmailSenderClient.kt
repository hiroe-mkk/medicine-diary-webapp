package example.testhelper.springframework.autoconfigure

import example.infrastructure.email.shared.*
import io.mockk.*
import org.springframework.context.annotation.*

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
