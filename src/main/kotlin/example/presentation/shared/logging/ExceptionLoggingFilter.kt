package example.presentation.shared.logging

import jakarta.servlet.*
import org.slf4j.*
import org.springframework.stereotype.*
import org.springframework.util.*
import org.springframework.web.filter.*
import java.io.*
import java.text.*

/**
 * ハンドリングされていない例外が発生したことを示すログを出力する Filter
 */
@Component
class ExceptionLoggingFilter : GenericFilterBean() {
    private var applicationLogger: Logger = LoggerFactory.getLogger(javaClass.name)

    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {
        try {
            filterChain.doFilter(servletRequest, servletResponse)
        } catch (ex: IOException) {
            log(ex)
            throw ex
        } catch (ex: ServletException) {
            log(ex)
            throw ex
        } catch (ex: RuntimeException) {
            log(ex)
            throw ex
        }
    }

    fun log(ex: Exception) {
        if (applicationLogger.isErrorEnabled) applicationLogger.error(message(ex), ex)
    }

    private fun message(ex: Exception): String? {
        return MessageFormat.format(String.format("{0} {1}"),
                                    ex.javaClass.simpleName,
                                    if (StringUtils.hasText(ex.message)) ex.message else "UNDEFINED-MESSAGE")
    }
}