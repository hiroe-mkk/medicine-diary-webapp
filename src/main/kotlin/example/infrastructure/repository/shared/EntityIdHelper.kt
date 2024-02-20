package example.infrastructure.repository.shared

import java.util.*

object EntityIdHelper {
    fun generate(): String {
        return UUID.randomUUID().toString()
    }
}