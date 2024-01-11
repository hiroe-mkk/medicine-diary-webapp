package example.presentation.shared.usersession

import example.domain.shared.exception.*

/**
 * 認証済みユーザのみを扱う箇所でユーザセッションが取得できなかったことを示す例外
 */
class UserSessionNotFoundException // TODO: より適切なクラス名がないか再考する
    : AssertionFailException("ユーザーセッションの取得に失敗しました。")