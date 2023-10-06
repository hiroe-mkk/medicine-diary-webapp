package example.presentation.controller.page

import example.domain.shared.exception.*
import jakarta.servlet.http.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.*

@Component
@ControllerAdvice("example.presentation.controller.page")
class PageControllerExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException::class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    fun handleResourceNotFoundException(ex: ResourceNotFoundException): String {
        return "error/notFoundError"
    }
}