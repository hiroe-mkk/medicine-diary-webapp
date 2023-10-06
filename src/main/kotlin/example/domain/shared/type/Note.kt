package example.domain.shared.type

data class Note(val value: String) {
    fun isBlank(): Boolean {
        return value.isBlank()
    }

    fun toLines(): List<String> {
        return value.split("\n")
    }

    override fun toString() = value
}