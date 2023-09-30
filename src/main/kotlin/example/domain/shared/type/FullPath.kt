package example.domain.shared.type

abstract class FullPath(val rootPath: String,
                        val relativePath: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FullPath
        if (rootPath != other.rootPath) return false
        if (relativePath != other.relativePath) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rootPath.hashCode()
        result = 31 * result + relativePath.hashCode()
        return result
    }

    override fun toString(): String = rootPath + relativePath
}