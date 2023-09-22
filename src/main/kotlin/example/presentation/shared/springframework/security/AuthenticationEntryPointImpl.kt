package example.presentation.shared.springframework.security

import jakarta.servlet.http.*
import org.springframework.security.core.*
import org.springframework.security.web.*

class AuthenticationEntryPointImpl : AuthenticationEntryPoint {
    /**
     * 未認証ユーザによる認証必須エンドポイントに対するリクエストが発生した場合に実行される
     */
    override fun commence(request: HttpServletRequest,
                          response: HttpServletResponse,
                          authException: AuthenticationException?) {
        response.sendRedirect("/")
    }
}