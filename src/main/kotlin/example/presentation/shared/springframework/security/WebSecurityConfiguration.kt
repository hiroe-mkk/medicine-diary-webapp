package example.presentation.shared.springframework.security

import example.presentation.shared.springframework.security.oauth2.oidc.*
import jakarta.servlet.http.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.autoconfigure.security.servlet.PathRequest.*
import org.springframework.context.annotation.*
import org.springframework.core.annotation.*
import org.springframework.http.*
import org.springframework.security.config.annotation.web.builders.*
import org.springframework.security.config.annotation.web.configuration.*
import org.springframework.security.config.web.server.ServerHttpSecurity.*
import org.springframework.security.core.userdetails.*
import org.springframework.security.crypto.bcrypt.*
import org.springframework.security.crypto.password.*
import org.springframework.security.provisioning.*
import org.springframework.security.web.*
import org.springframework.security.web.authentication.*

@Configuration
@EnableWebSecurity
class WebSecurityConfiguration(private val oidcUserAccountService: OidcUserAccountService) {
    @Value("\${user.admin.username}")
    lateinit var adminUsername: String

    @Value("\${user.admin.password}")
    lateinit var adminPassword: String

    @Bean
    @Order(1)
    fun actuatorFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.securityMatcher("/actuator/**")
            .httpBasic {
                it.realmName("Actuator and Admin Realm")
            }.authorizeHttpRequests {
                it.requestMatchers("/actuator/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            }
        return http.build()
    }

    @Bean
    @Order(2)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.oauth2Login {
            it.userInfoEndpoint { oidcUserAccountService }
                .successHandler(AuthenticationSuccessHandlerImpl())
                .failureHandler(AuthenticationFailureHandlerImpl())
        }.authorizeHttpRequests {
            it.requestMatchers(toStaticResources().atCommonLocations()).permitAll() // 静的リソース
                .requestMatchers("/dist/**").permitAll() // frontend サブプロジェクトのビルド成果物が格納されているディレクトリ
                .requestMatchers("/", "/about", "/agreement", "/login").permitAll()
                .anyRequest().authenticated()
        }.exceptionHandling {
            it.authenticationEntryPoint(AuthenticationEntryPointImpl())
        }.logout {
            it.logoutSuccessHandler(LogoutSuccessHandlerImpl())
        }
        return http.build()
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer {
            it.ignoring().requestMatchers("/actuator/health")
        }
    }

    @Bean
    fun userDetailsService(passwordEncoder: PasswordEncoder): InMemoryUserDetailsManager? {
        // 管理者用のデフォルトユーザー
        // TODO: 永続化するか検討する
        val user = User.withUsername(adminUsername)
            .password(passwordEncoder.encode(adminPassword))
            .roles("ADMIN")
            .build()
        return InMemoryUserDetailsManager(user)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder? {
        return BCryptPasswordEncoder()
    }
}