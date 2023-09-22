package example.presentation.shared.springframework.security

import org.springframework.boot.autoconfigure.security.servlet.PathRequest.*
import org.springframework.context.annotation.*
import org.springframework.security.config.annotation.web.builders.*
import org.springframework.security.config.annotation.web.configuration.*
import org.springframework.security.web.*

@Configuration
@EnableWebSecurity
class WebSecurityConfiguration {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests {
            it.requestMatchers(toStaticResources().atCommonLocations()).permitAll() // 静的リソース
                .requestMatchers("/dist/**").permitAll() // frontend サブプロジェクトのビルド成果物が格納されているディレクトリ
                .requestMatchers("/").permitAll()
                .anyRequest().authenticated()
        }.oauth2Login {
            it.defaultSuccessUrl("/mypage")
            it.failureUrl("/")
        }.exceptionHandling {
            it.authenticationEntryPoint(AuthenticationEntryPointImpl())
        }

        return http.build()
    }
}