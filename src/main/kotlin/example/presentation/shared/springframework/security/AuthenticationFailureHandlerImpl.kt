package example.presentation.shared.springframework.security

import example.domain.shared.message.*
import example.presentation.shared.springframework.mvc.*
import jakarta.servlet.http.*
import org.slf4j.*
import org.springframework.security.core.*
import org.springframework.security.web.authentication.*
import org.springframework.stereotype.*

@Component
class AuthenticationFailureHandlerImpl : AuthenticationFailureHandler {
    private val logger = LoggerFactory.getLogger(AuthenticationFailureHandlerImpl::class.java)

    /**
     * 認証に失敗した場合に実行される
     */
    override fun onAuthenticationFailure(request: HttpServletRequest,
                                         response: HttpServletResponse,
                                         exception: AuthenticationException?) {
        logger.info("Authentication Failure IP_ADDRESS=[{}]", request.remoteAddr)

        val redirectResolver = RedirectResolver(request, response)
        redirectResolver.addFlashAttribute("resultMessage",
                                           ResultMessage.error("ログインに失敗しました。"))
        redirectResolver.sendRedirect("/")
    }
}