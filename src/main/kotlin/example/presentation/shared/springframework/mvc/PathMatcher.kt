package example.presentation.shared.springframework.mvc

import org.springframework.http.server.*
import org.springframework.web.util.pattern.*

object PathMatcher { // TODO: より分かりやすいクラス名がないか再考する
    fun isApiCall(path: String): Boolean {
        return isPathMatch(path, "/api/**")
    }

    fun isActuatorCall(path: String): Boolean { //TODO: 認証失敗時に、既に /error にリダイレクトされている理由を調べる
        return isPathMatch(path, "/actuator/**") || isPathMatch(path, "/error")
    }

    private fun isPathMatch(path: String, pathPattern: String): Boolean {
        val pathPattern = PathPatternParser().parse(pathPattern)
        val pathContainer = PathContainer.parsePath(path)
        return pathPattern.matches(pathContainer)
    }
}