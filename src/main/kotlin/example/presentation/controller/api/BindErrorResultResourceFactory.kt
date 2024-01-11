package example.presentation.controller.api

import org.springframework.context.*
import org.springframework.context.i18n.*
import org.springframework.stereotype.*
import org.springframework.validation.*

@Component
class BindErrorResultResourceFactory(private val messageSource: MessageSource) {
    fun create(bindingResult: BindingResult): JSONBindErrorResponse {
        val fieldErrors = bindingResult.fieldErrors.groupBy({ extractName(it) },
                                                            { extractMessage(it) })
        return JSONBindErrorResponse(fieldErrors)
    }

    private fun extractName(fieldError: FieldError): String {
        return fieldError.field.replace(Regex("\\..+"), "")
    }

    private fun extractMessage(fieldError: FieldError): String {
        return messageSource.getMessage(fieldError, LocaleContextHolder.getLocale())
    }
}