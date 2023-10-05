package example.domain.shared.type

abstract class URL(val endpoint: String,
                   val path: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as URL
        if (endpoint != other.endpoint) return false
        if (path != other.path) return false

        return true
    }

    override fun hashCode(): Int {
        var result = endpoint.hashCode()
        result = 31 * result + path.hashCode()
        return result
    }

    override fun toString(): String = endpoint + path
}