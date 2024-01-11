package example.testhelper.springframework.autoconfigure

import org.springframework.boot.test.autoconfigure.web.servlet.*
import org.springframework.boot.test.context.*
import org.springframework.transaction.annotation.*

/**
 * Controller クラスをテストする場合に利用可能なアノテーション
 */
@SpringBootTest
@UseMockObjectStorageClient
@UseMockEmailSenderClient
@EnableTestDataInserterAutoConfiguration
@Transactional
@AutoConfigureMockMvc
annotation class ControllerTest