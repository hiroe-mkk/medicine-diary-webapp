package example.infrastructure.db.repository.shared

import example.domain.shared.type.*
import java.util.*

object EntityIdHelper {
    fun generate(): String {
        return UUID.randomUUID().toString()
    }

    fun isValid(entityId: EntityId): Boolean {
        return try {
            UUID.fromString(entityId.value)
            true
        } catch (ex: IllegalArgumentException) {
            false
        }
    }
}
