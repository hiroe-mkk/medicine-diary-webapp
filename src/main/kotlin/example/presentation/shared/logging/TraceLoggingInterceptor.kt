package example.presentation.shared.logging

import jakarta.servlet.http.*
import org.slf4j.*
import org.springframework.web.method.*
import org.springframework.web.servlet.*

/**
 * Controller の処理開始、終了時にログ出力する HandlerInterceptor
 */
class TraceLoggingInterceptor : HandlerInterceptor {
    private val logger = LoggerFactory.getLogger(TraceLoggingInterceptor::class.java)

    private val START_ATTR = TraceLoggingInterceptor::class.java.name + ".startTime"

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod) {
            val startTime = System.nanoTime()
            request.setAttribute(START_ATTR, startTime)

            if (logger.isTraceEnabled) {
                logger.trace("Start Controller ({}.{})", className(handler), methodName(handler))
            }
        }

        return true
    }

    override fun postHandle(request: HttpServletRequest,
                            response: HttpServletResponse,
                            handler: Any,
                            modelAndView: ModelAndView?) {
        if (handler !is HandlerMethod || !logger.isTraceEnabled) return

        val startTime = request.getAttribute(START_ATTR)
                            ?.let {
                                request.removeAttribute(START_ATTR)
                                it as Long
                            } ?: 0L
        val handlingTime = String.format("%,3d", System.nanoTime() - startTime)
        val format = "End Controller ({}.{}) {}ns"
        if (modelAndView == null) {
            logger.trace(format, className(handler), methodName(handler), handlingTime)
        } else {
            val view = modelAndView.view ?: modelAndView.viewName
            val model = modelAndView.model
            logger.trace("$format -> view={}, model={}",
                         className(handler), methodName(handler), handlingTime, view, model)
        }
    }

    private fun methodName(handler: HandlerMethod): String = handler.method.name

    private fun className(handler: HandlerMethod): String = handler.method.declaringClass.simpleName
}