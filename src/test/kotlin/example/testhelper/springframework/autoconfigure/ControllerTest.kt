package example.testhelper.springframework.autoconfigure

import org.springframework.beans.factory.config.*
import org.springframework.boot.test.autoconfigure.jdbc.*
import org.springframework.boot.test.autoconfigure.web.servlet.*
import org.springframework.boot.test.context.*
import org.springframework.context.annotation.*
import org.springframework.context.support.*
import org.springframework.transaction.annotation.*

/**
 * Controller クラスをテストする場合に利用可能なアノテーション
 */
@SpringBootTest
@UseMockObjectStorageClient
@UseMockEmailSenderClient
@Import(TestDataInserterAutoConfiguration::class, ControllerTest.Configuration::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 組み込みデータベースへの置き換えを無効化する
@Transactional
@AutoConfigureMockMvc
annotation class ControllerTest {
    class Configuration {
        @Bean
        fun customScopeConfigurer(): CustomScopeConfigurer {
            val configurer = CustomScopeConfigurer()
            configurer.addScope("session", SimpleThreadScope())
            return configurer
        }
    }
}
