package example

import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*

@SpringBootApplication
class Application {
	companion object {
		@JvmStatic
		fun main(args: Array<String>) {
			runApplication<Application>(*args)
		}
	}
}