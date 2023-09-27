package example.presentation.shared.springframework.mvc

import org.springframework.http.server.*
import org.springframework.web.util.pattern.*

object PathMatcher { // TODO: より分かりやすいクラス名がないか再考する
    fun isApiCall(path: String): Boolean {
        val pathPattern = PathPatternParser().parse("/api/**")
        val pathContainer = PathContainer.parsePath(path)
        return pathPattern.matches(pathContainer)
    }
}