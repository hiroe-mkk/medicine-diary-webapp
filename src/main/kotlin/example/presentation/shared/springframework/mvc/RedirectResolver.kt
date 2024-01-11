package example.presentation.shared.springframework.mvc

import jakarta.servlet.http.*
import org.springframework.web.servlet.*
import org.springframework.web.servlet.support.*

/**
 * Controller クラス以外の場所からリダイレクトする手段を提供する
 */
class RedirectResolver(private val request: HttpServletRequest,
                       private val response: HttpServletResponse) {
    private val flashMap: FlashMap = FlashMap()

    fun addFlashAttribute(attributeName: String, attributeValue: Any) {
        flashMap[attributeName] = attributeValue
    }

    fun sendRedirect(location: String) {
        saveFlashMap()
        response.sendRedirect(location)
    }

    private fun saveFlashMap() {
        val flashMapManager = RequestContextUtils.getFlashMapManager(request) ?: SessionFlashMapManager()
        flashMapManager.saveOutputFlashMap(flashMap, request, response)
    }
}