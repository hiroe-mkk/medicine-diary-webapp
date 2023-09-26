package example.testhelper.springframework.autoconfigure

import org.springframework.boot.test.autoconfigure.web.servlet.*
import org.springframework.boot.test.context.*

/**
 * Controller クラスをテストする場合に利用可能なアノテーション
 */
@SpringBootTest
@EnableTestDataInserter
@AutoConfigureMockMvc
annotation class ControllerTest