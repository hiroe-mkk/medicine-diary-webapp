package example.presentation.controller.page

import example.domain.shared.exception.*
import example.presentation.shared.usersession.*
import jakarta.servlet.http.*
import org.springframework.http.*
import org.springframework.security.core.context.*
import org.springframework.security.web.authentication.logout.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Component
@ControllerAdvice("example.presentation.controller.page")
class PageControllerExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException::class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    fun handleResourceNotFoundException(ex: ResourceNotFoundException): String {
        return "error/notFoundError"
    }

    @ExceptionHandler(DomainException::class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    fun handleBusinessException(ex: DomainException, model: Model): String {
        model.addAttribute("errorMessage", ex.resultMessage.message)
        model.addAttribute("errorDetails", ex.resultMessage.details)
        return "error/messageNotificationError"
    }

    @ExceptionHandler(ResultMessageNotificationException::class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    fun handleErrorMessageNotificationException(ex: ResultMessageNotificationException, model: Model): String {
        model.addAttribute("errorMessage", ex.resultMessage.message)
        model.addAttribute("errorDetails", ex.resultMessage.details)
        return "error/messageNotificationError"
    }

    @ExceptionHandler(UserSessionNotFoundException::class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    fun handleUserSessionNotFoundException(httpServletRequest: HttpServletRequest,
                                           httpServletResponse: HttpServletResponse): String {
        val logoutHandler = SecurityContextLogoutHandler()
        logoutHandler.logout(httpServletRequest,
                             httpServletResponse,
                             SecurityContextHolder.getContext().authentication)
        return "error/error"
    }
}