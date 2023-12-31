package example.presentation.controller

import example.presentation.controller.api.*
import jakarta.servlet.*
import jakarta.servlet.http.*
import org.springframework.boot.web.servlet.error.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.*

/**
 * Spring MVC 管轄外で発生した例外をハンドリングする
 */
@Controller
@RequestMapping("/error")
class ErrorControllerImpl : ErrorController {
    @RequestMapping(produces = [MediaType.TEXT_HTML_VALUE])
    fun handleExceptionOccurredInPageController(request: HttpServletRequest): ModelAndView {
        val httpStatus = getHttpStatus(request)
        return ModelAndView().apply {
            status = httpStatus
            viewName = if (httpStatus == HttpStatus.NOT_FOUND) {
                "error/notFoundError"
            } else {
                "error/error"
            }
        }
    }

    @RequestMapping
    fun handleExceptionOccurredInApiController(request: HttpServletRequest): ResponseEntity<Any> {
        val httpStatus = getHttpStatus(request)
        val errorResultResource = if (httpStatus == HttpStatus.NOT_FOUND) {
            JSONErrorResponse.create("このページはご利用いただけません。")
        } else {
            JSONErrorResponse.create("システムエラーが発生しました。")
        }
        return ResponseEntity.status(httpStatus).body(errorResultResource)
    }

    private fun getHttpStatus(request: HttpServletRequest): HttpStatus {
        return HttpStatus.valueOf(getStatusCode(request))
    }

    private fun getStatusCode(request: HttpServletRequest): Int {
        return request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE) as? Int ?: 500
    }
}