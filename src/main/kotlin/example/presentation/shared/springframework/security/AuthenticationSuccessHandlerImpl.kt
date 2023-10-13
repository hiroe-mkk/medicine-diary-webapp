package example.presentation.shared.springframework.security

import example.domain.shared.message.*
import example.presentation.shared.springframework.mvc.*
import jakarta.servlet.http.*
import org.springframework.http.server.*
import org.springframework.security.core.*
import org.springframework.security.web.authentication.*
import org.springframework.security.web.savedrequest.*
import org.springframework.web.util.pattern.*
import java.net.*

class AuthenticationSuccessHandlerImpl : AuthenticationSuccessHandler {
    /**
     * 認証に成功した場合に実行される
     */
    override fun onAuthenticationSuccess(request: HttpServletRequest,
                                         response: HttpServletResponse,
                                         authentication: Authentication?) {
        val redirectResolver = RedirectResolver(request, response)
        redirectResolver.addFlashAttribute("resultMessage",
                                           ResultMessage.info("ログインに成功しました。"))
        val redirectPath = getRedirectPath(request, response)
        redirectResolver.sendRedirect(redirectPath)
    }

    private fun getRedirectPath(request: HttpServletRequest, response: HttpServletResponse): String {
        val lastRequestedURI = getPathOfLastRequestedPath(request, response)?.let { excludeErrorPagePaths(it) }
        return lastRequestedURI ?: "/mypage"
    }

    private fun getPathOfLastRequestedPath(request: HttpServletRequest, response: HttpServletResponse): String? {
        val savedRequest = HttpSessionRequestCache().getRequest(request, response)
        val redirectUrl = savedRequest?.redirectUrl // クライアントによって要求された最後の URI
        return redirectUrl?.let { URL(it).path } // URL のうちのパス部分のみを抽出する
    }

    private fun excludeErrorPagePaths(path: String): String? {
        val pathPattern = PathPatternParser().parse("/error/**")
        val pathContainer = PathContainer.parsePath(path)
        return if (pathPattern.matches(pathContainer)) null else path
    }
}