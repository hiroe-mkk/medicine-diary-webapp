package example.presentation.shared.logging

import jakarta.servlet.http.*
import org.slf4j.*
import org.springframework.web.method.*
import org.springframework.web.servlet.*
import java.lang.reflect.*
import java.util.concurrent.*

/**
 * Controller の処理開始、終了時にログ出力する HandlerInterceptor
 */
class TraceLoggingInterceptor : HandlerInterceptor {
    private val logger = LoggerFactory.getLogger(TraceLoggingInterceptor::class.java)

    private val START_ATTR = TraceLoggingInterceptor::class.java.name + ".startTime"
    private val HANDLING_ATTR = TraceLoggingInterceptor::class.java.name + ".handlingTime"
    private var WARN_NANOS: Long = TimeUnit.SECONDS.toNanos(3L) // Controller の処理に3秒以上かかった場合に WARN ログを出力する

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler !is HandlerMethod) return true

        val startTime = System.nanoTime()
        request.setAttribute(START_ATTR, startTime)

        if (logger.isTraceEnabled) {
            handlingStartController(handler.method, handler)
        }

        return true
    }

    override fun postHandle(request: HttpServletRequest,
                            response: HttpServletResponse,
                            handler: Any,
                            modelAndView: ModelAndView?) {
        if (handler !is HandlerMethod) return

        val startTime = request.getAttribute(START_ATTR) as? Long ?: 0L
        val handlingTime = System.nanoTime() - startTime
        request.removeAttribute(START_ATTR)
        request.setAttribute(HANDLING_ATTR, handlingTime)

        if (logger.isTraceEnabled || (isWarnHandling(handlingTime) && logger.isWarnEnabled)) {
            handlingEndController(handler.method, handler, modelAndView)
            handlingTime(handlingTime, handler.method, handler)
        }
    }

    private fun handlingStartController(method: Method, handler: HandlerMethod) {
        logger.trace("[START CONTROLLER] {}.{}({})",
                     method.declaringClass.simpleName,
                     method.name,
                     buildMethodParams(handler))
    }

    private fun handlingEndController(method: Method,
                                      handler: HandlerMethod,
                                      modelAndView: ModelAndView?) {
        val format = "[END CONTROLLER] {}.{}({})"
        if (modelAndView == null) {
            logger.trace(format,
                         method.declaringClass.simpleName,
                         method.name,
                         buildMethodParams(handler))
        } else {
            logger.trace("$format -> view={}, model={}",
                         method.declaringClass.simpleName,
                         method.name,
                         buildMethodParams(handler),
                         modelAndView?.view ?: modelAndView?.viewName,
                         modelAndView?.model)
        }
    }

    private fun handlingTime(handlingTime: Long, method: Method, handler: HandlerMethod) {
        val formattedHandlingTime = String.format("%1$,3d", handlingTime)
        val format = "[HANDLING TIME] {}.{}({}): {} ns"
        if (isWarnHandling(handlingTime)) {
            logger.warn("$format > {}",
                        method.declaringClass.simpleName,
                        method.name,
                        buildMethodParams(handler),
                        formattedHandlingTime,
                        WARN_NANOS)
        } else {
            logger.trace(format,
                         method.declaringClass.simpleName,
                         method.name,
                         buildMethodParams(handler),
                         formattedHandlingTime)
        }
    }

    private fun isWarnHandling(handlingTime: Long) = handlingTime > WARN_NANOS

    private fun buildMethodParams(handlerMethod: HandlerMethod): String {
        return handlerMethod.methodParameters.joinToString(",") { it.parameterType.simpleName }
    }
}