package example.domain.shared.message

/**
 * 処理実行結果をユーザに通知するために使用する
 */
data class ResultMessage private constructor(val type: Type,
                                             val message: String,
                                             val details: String?) {
    enum class Type {
        /**
         * ユーザの操作による処理が正常に実行された場合の結果メッセージのタイプ
         */
        INFO,

        /**
         * 何らかの問題があるが、処理を続行できる場合の結果メッセージのタイプ
         */
        WARNING,

        /**
         * 業務ロジックでのエラーやシステム起因のエラーと判定された場合の結果メッセージのタイプ
         */
        ERROR
    }

    companion object {
        fun info(message: String, details: String? = null): ResultMessage {
            return ResultMessage(Type.INFO, message, details)
        }

        fun warning(message: String, details: String? = null): ResultMessage {
            return ResultMessage(Type.WARNING, message, details)
        }

        fun error(message: String, details: String? = null): ResultMessage {
            return ResultMessage(Type.ERROR, message, details)
        }
    }
}