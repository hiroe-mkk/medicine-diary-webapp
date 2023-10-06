package example.domain.shared.type

import java.time.*

interface LocalDateTimeProvider {
    fun now(): LocalDateTime
}