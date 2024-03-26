package example.presentation.controller.api

import example.application.shared.exception.*
import example.domain.shared.exception.*
import example.presentation.shared.usersession.*
import jakarta.servlet.http.*
import org.springframework.http.*
import org.springframework.security.core.context.*
import org.springframework.security.web.authentication.logout.*
import org.springframework.stereotype.*
import org.springframework.validation.*
import org.springframework.web.bind.annotation.*

@Component
@ControllerAdvice("example.presentation.controller.api")
class ApiControllerExceptionHandler(private val bindErrorResponseFactory: BindErrorResultResourceFactory) {
    @ExceptionHandler(BindException::class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handleBindException(ex: BindException): JSONBindErrorResponse {
        return bindErrorResponseFactory.create(ex.bindingResult)
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    fun handleResourceNotFoundException(ex: ResourceNotFoundException): JSONErrorResponse {
        return JSONErrorResponse.create(ex)
    }

    @ExceptionHandler(InvalidEntityIdException::class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handleInvalidEntityIdException(ex: InvalidEntityIdException): JSONErrorResponse {
        return JSONErrorResponse.create(ex)
    }

    @ExceptionHandler(DomainException::class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ResponseBody
    fun handleBusinessException(ex: DomainException): JSONErrorResponse {
        return JSONErrorResponse.create(ex)
    }

    @ExceptionHandler(ResultMessageNotificationException::class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handleErrorMessageNotificationException(ex: ResultMessageNotificationException): JSONErrorResponse {
        return JSONErrorResponse.create(ex)
    }

    @ExceptionHandler(MissingUserSessionException::class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ResponseBody
    fun handleUserSessionNotFoundException(httpServletRequest: HttpServletRequest,
                                           httpServletResponse: HttpServletResponse) {
        val logoutHandler = SecurityContextLogoutHandler()
        logoutHandler.logout(httpServletRequest,
                             httpServletResponse,
                             SecurityContextHolder.getContext().authentication)
    }
}