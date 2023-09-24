package example.presentation.shared.springframework.security

import example.domain.shared.message.*
import example.presentation.shared.springframework.mvc.*
import jakarta.servlet.http.*
import org.springframework.security.core.*
import org.springframework.security.web.authentication.*

class AuthenticationFailureHandlerImpl : AuthenticationFailureHandler {
    /**
     * 認証に失敗した場合に実行される
     */
    override fun onAuthenticationFailure(request: HttpServletRequest,
                                         response: HttpServletResponse,
                                         exception: AuthenticationException?) {
        val redirectResolver = RedirectResolver(request, response)
        redirectResolver.addFlashAttribute("resultMessage",
                                           ResultMessage.error("ログインに失敗しました。"))
        redirectResolver.sendRedirect("/")
    }
}