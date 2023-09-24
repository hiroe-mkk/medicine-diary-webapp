package example.presentation.shared.springframework.security

import example.domain.shared.message.*
import example.presentation.shared.springframework.mvc.*
import jakarta.servlet.http.*
import org.springframework.security.core.*
import org.springframework.security.web.authentication.*

class AuthenticationSuccessHandlerImpl : AuthenticationSuccessHandler {
    /**
     * 認証に成功した場合に実行される
     */
    override fun onAuthenticationSuccess(request: HttpServletRequest,
                                         response: HttpServletResponse,
                                         authentication: Authentication?) {
        // TODO: 認証リクエスト以前にクライアントによって要求された最後のパスにリダイレクトするように修正する
        val redirectResolver = RedirectResolver(request, response)
        redirectResolver.addFlashAttribute("resultMessage",
                                           ResultMessage.info("ログインに成功しました。"))
        redirectResolver.sendRedirect("/mypage")
    }
}