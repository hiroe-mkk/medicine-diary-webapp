package example.presentation.shared.springframework.security

import example.domain.shared.message.*
import example.presentation.shared.springframework.mvc.*
import jakarta.servlet.http.*
import org.slf4j.*
import org.springframework.security.core.*
import org.springframework.security.web.authentication.*

class AuthenticationSuccessHandlerImpl : AuthenticationSuccessHandler {
    private val logger = LoggerFactory.getLogger(AuthenticationSuccessHandlerImpl::class.java)

    /**
     * 認証に成功した場合に実行される
     */
    override fun onAuthenticationSuccess(request: HttpServletRequest,
                                         response: HttpServletResponse,
                                         authentication: Authentication?) {
        val account = authentication?.principal as? AuthenticatedAccount
        logger.info("Authentication Success ACCOUNT_ID=[{}] IP_ADDRESS=[{}]", account?.id, request.remoteAddr)

        val redirectResolver = RedirectResolver(request, response)
        redirectResolver.addFlashAttribute("resultMessage",
                                           ResultMessage.info("ログインに成功しました。"))
        redirectResolver.sendRedirect("/")
    }
}