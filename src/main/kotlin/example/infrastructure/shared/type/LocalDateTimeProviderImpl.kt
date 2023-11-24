package example.infrastructure.shared.type

import example.domain.shared.type.*
import org.springframework.stereotype.*
import java.time.*
import java.time.temporal.*

@Component
class LocalDateTimeProviderImpl : LocalDateTimeProvider {
    override fun now(): LocalDateTime {
        return LocalDateTime.now(ZoneId.of("Asia/Tokyo")).truncatedTo(ChronoUnit.MINUTES)
    }
}