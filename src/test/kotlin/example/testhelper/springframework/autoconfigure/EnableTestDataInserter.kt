package example.testhelper.springframework.autoconfigure

import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.testhelper.inserter.*
import org.springframework.context.annotation.*
import org.springframework.stereotype.*

/**
 * モックされた TestDataInserter の AutoConfiguration を有効にするアノテーション
 */
@Import(EnableTestDataInserter.Configuration::class)
annotation class EnableTestDataInserter {
    class Configuration(private val accountRepository: AccountRepository,
                        private val profileRepository: ProfileRepository) {
        @Bean
        fun testAccountInserter(): TestAccountInserter = TestAccountInserter(accountRepository, profileRepository)
    }
}