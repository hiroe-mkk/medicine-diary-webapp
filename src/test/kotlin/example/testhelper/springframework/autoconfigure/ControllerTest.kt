package example.testhelper.springframework.autoconfigure

import org.springframework.boot.test.autoconfigure.web.servlet.*
import org.springframework.boot.test.context.*
import org.springframework.transaction.annotation.Transactional

/**
 * Controller クラスをテストする場合に利用可能なアノテーション
 */
@SpringBootTest
@EnableMockObjectStorageClient
@EnableTestDataInserter
@Transactional
@AutoConfigureMockMvc
annotation class ControllerTest