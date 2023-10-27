package example.presentation.controller.api

import example.domain.shared.exception.*
import jakarta.servlet.http.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.validation.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.*

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

    @ExceptionHandler(DomainException::class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ResponseBody
    fun handleBusinessException(ex: DomainException): JSONErrorResponse {
        return JSONErrorResponse.create(ex)
    }
}