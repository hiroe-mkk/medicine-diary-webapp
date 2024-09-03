package example.infrastructure.shared.exception

/**
 * インフラストラクチャ層で技術的な問題が発生したことを示す例外クラス
 */
abstract class InfrastructureException(message: String, cause: Throwable? = null)
    : Exception(message, cause)
