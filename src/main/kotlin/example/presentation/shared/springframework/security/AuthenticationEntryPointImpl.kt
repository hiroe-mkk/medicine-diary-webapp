package example.presentation.shared.springframework.security

import example.domain.shared.message.*
import example.presentation.shared.springframework.mvc.*
import jakarta.servlet.http.*
import org.springframework.security.core.*
import org.springframework.security.web.*
import org.springframework.stereotype.*

@Component
class AuthenticationEntryPointImpl : AuthenticationEntryPoint {
    /**
     * 未認証ユーザによる認証必須エンドポイントに対するリクエストが発生した場合に実行される
     */
    override fun commence(request: HttpServletRequest,
                          response: HttpServletResponse,
                          authException: AuthenticationException?) {
        // TODO: 存在しないエンドポイントに対するリクエストの場合は、NotFoundエラー画面にリダイレクトするように修正する
        if (PathMatcher.isApiCall(request.requestURI)) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
        } else {
            val redirectResolver = RedirectResolver(request, response)
            redirectResolver.addFlashAttribute("resultMessage",
                                               ResultMessage.error("認証情報の取得に失敗しました。"))
            redirectResolver.sendRedirect("/")
        }
    }
}