package example.presentation.shared.session

/**
 * 最後にリクエストされた画面のパスを保持する
 *
 * セッションに格納し、コマンドを実行する画面以外がリクエストされたときに更新される。
 */
data class LastRequestedPagePath(val value: String?)