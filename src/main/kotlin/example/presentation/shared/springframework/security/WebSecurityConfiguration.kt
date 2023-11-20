package example.presentation.shared.springframework.security

import example.presentation.shared.springframework.security.oauth2.oidc.*
import org.springframework.boot.autoconfigure.security.servlet.PathRequest.*
import org.springframework.context.annotation.*
import org.springframework.security.config.annotation.web.builders.*
import org.springframework.security.config.annotation.web.configuration.*
import org.springframework.security.web.*

@Configuration
@EnableWebSecurity
class WebSecurityConfiguration(private val oidcUserAccountService: OidcUserAccountService) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests {
            it.requestMatchers(toStaticResources().atCommonLocations()).permitAll() // 静的リソース
                .requestMatchers("/dist/**").permitAll() // frontend サブプロジェクトのビルド成果物が格納されているディレクトリ
                .requestMatchers("/", "/about/**", "/service/**").permitAll()
                .anyRequest().authenticated()
        }.oauth2Login {
            it.userInfoEndpoint { oidcUserAccountService }
                .successHandler(AuthenticationSuccessHandlerImpl())
                .failureHandler(AuthenticationFailureHandlerImpl())
        }.exceptionHandling {
            it.authenticationEntryPoint(AuthenticationEntryPointImpl())
        }.logout {
            it.logoutSuccessHandler(LogoutSuccessHandlerImpl())
        }

        return http.build()
    }
}