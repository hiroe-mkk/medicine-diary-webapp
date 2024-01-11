package example.domain.shared.type

abstract class EntityId(val value: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return value == (other as EntityId).value
    }

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = value
}