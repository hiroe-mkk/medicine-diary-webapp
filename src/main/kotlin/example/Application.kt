package example

import example.infrastructure.storage.shared.objectstrage.minio.*
import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.boot.context.properties.*

@SpringBootApplication
@EnableConfigurationProperties(MinioProperties::class)
class Application {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<Application>(*args)
        }
    }
}