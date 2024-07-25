package example.infrastructure.db.repository.shared

data class OrderedEntity<T>(val ordering: Int, val value: T)
