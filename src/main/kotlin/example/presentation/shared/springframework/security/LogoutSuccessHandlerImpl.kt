package example.presentation.shared.springframework.security

import example.domain.shared.message.*
import example.presentation.shared.springframework.mvc.*
import jakarta.servlet.http.*
import org.slf4j.*
import org.springframework.security.core.*
import org.springframework.security.web.authentication.logout.*

class LogoutSuccessHandlerImpl : LogoutSuccessHandler {
    private val logger = LoggerFactory.getLogger(LogoutSuccessHandlerImpl::class.java)

    /**
     * ログアウトに成功した場合に実行される
     */
    override fun onLogoutSuccess(request: HttpServletRequest,
                                 response: HttpServletResponse,
                                 authentication: Authentication?) {
        val account = authentication?.principal as? AuthenticatedAccount
        logger.info("ACCOUNT_ID #{} logoutSuccess", account?.id)

        val redirectResolver = RedirectResolver(request, response)
        redirectResolver.addFlashAttribute("resultMessage",
                                           ResultMessage.info("ログアウトに成功しました。"))
        redirectResolver.sendRedirect("/")
    }
}