package example.presentation.shared.session

import org.springframework.context.annotation.*
import org.springframework.stereotype.*
import org.springframework.web.context.*

/**
 * 最後にリクエストされた画面のパスを保持する
 *
 * セッションに格納し、コマンドを実行する画面以外がリクエストされたときに更新される。
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
data class LastRequestedPage(var path: String = "/")
