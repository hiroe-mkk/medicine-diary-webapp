package example.domain.shared.type

data class Note(val value: String) {
    override fun toString() = value
}