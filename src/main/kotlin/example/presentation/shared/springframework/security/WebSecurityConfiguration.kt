package example.presentation.shared.springframework.security

import org.springframework.beans.factory.annotation.*
import org.springframework.boot.autoconfigure.security.servlet.PathRequest.*
import org.springframework.context.annotation.*
import org.springframework.core.annotation.*
import org.springframework.security.config.annotation.web.builders.*
import org.springframework.security.config.annotation.web.configuration.*
import org.springframework.security.core.userdetails.*
import org.springframework.security.crypto.bcrypt.*
import org.springframework.security.crypto.password.*
import org.springframework.security.oauth2.client.oidc.userinfo.*
import org.springframework.security.oauth2.client.userinfo.*
import org.springframework.security.oauth2.core.user.*
import org.springframework.security.provisioning.*
import org.springframework.security.web.*
import org.springframework.security.web.authentication.*
import org.springframework.security.web.authentication.logout.*

@Configuration
@EnableWebSecurity
class WebSecurityConfiguration {
    @Value("\${application.admin.username}")
    lateinit var adminUsername: String

    @Value("\${application.admin.password}")
    lateinit var adminPassword: String

    @Bean
    @Order(1)
    fun actuatorFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.securityMatcher("/actuator/**")
            .authorizeHttpRequests {
                it.requestMatchers("/actuator/health").permitAll()
                    .requestMatchers("/actuator/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            }.httpBasic {
                it.realmName("Actuator and Admin Realm")
            }
        return http.build()
    }

    @Bean
    @Order(2)
    fun filterChain(http: HttpSecurity,
                    authenticationSuccessHandler: AuthenticationSuccessHandler,
                    authenticationFailureHandler: AuthenticationFailureHandler,
                    authenticationEntryPoint: AuthenticationEntryPoint,
                    logoutSuccessHandler: LogoutSuccessHandler,
                    oAuth2UserService: OAuth2UserService<OAuth2UserRequest, OAuth2User>,
                    oidcUserService: OidcUserService): SecurityFilterChain {
        http.authorizeHttpRequests {
            it.requestMatchers(toStaticResources().atCommonLocations()).permitAll() // 静的リソース
                .requestMatchers("/dist/**").permitAll() // frontend サブプロジェクトのビルド成果物が格納されているディレクトリ
                .requestMatchers("/", "/about", "/agreement", "/contact", "/notice", "/login").permitAll()
                .anyRequest().authenticated()
        }.oauth2Login {
            it.userInfoEndpoint { endpointConfig ->
                endpointConfig
                    .oidcUserService(oidcUserService)
                    .userService(oAuth2UserService)
            }
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
        }.exceptionHandling {
            it.authenticationEntryPoint(authenticationEntryPoint)
        }.logout {
            it.logoutSuccessHandler(logoutSuccessHandler)
        }
        return http.build()
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
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}