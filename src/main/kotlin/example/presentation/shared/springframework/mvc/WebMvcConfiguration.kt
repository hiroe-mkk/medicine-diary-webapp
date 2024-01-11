package example.presentation.shared.springframework.mvc

import example.presentation.shared.logging.*
import org.springframework.context.annotation.*
import org.springframework.web.servlet.config.annotation.*

@Configuration
class WebMvcConfiguration : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(TraceLoggingInterceptor())
    }
}