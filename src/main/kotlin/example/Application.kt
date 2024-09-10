package example

import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.scheduling.annotation.*

@SpringBootApplication
@EnableScheduling
class Application {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<Application>(*args)
        }
    }
}
