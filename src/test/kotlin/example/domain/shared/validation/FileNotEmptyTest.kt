package example.domain.shared.validation

import example.testhelper.factory.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.junit.jupiter.params.*
import org.junit.jupiter.params.provider.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.autoconfigure.validation.*
import org.springframework.test.context.junit.jupiter.*
import org.springframework.validation.*
import org.springframework.web.multipart.*

@SpringJUnitConfig(classes = [ValidationAutoConfiguration::class])
internal class FileNotEmptyTest(@Autowired val validator: Validator) {
    @ParameterizedTest
    @CsvSource("0, true",
               "1, false")
    fun canDetectInvalidMultipartFile(size: Int, result: Boolean) {
        //given:
        val file = TestImageFactory.createMultipartFile(size = size)
        val form = Form(file)
        val bindException = BindException(form, "form")

        //when:
        validator.validate(form, bindException)

        //then:
        val bindingResult = bindException.bindingResult
        assertThat(bindingResult.hasFieldErrors("file")).isEqualTo(result)
    }

    @Test
    fun nullIsInvalidMultipartFile() {
        //given:
        val form = Form(null)
        val bindException = BindException(form, "form")

        //when:
        validator.validate(form, bindException)

        //then:
        val bindingResult = bindException.bindingResult
        assertThat(bindingResult.hasFieldErrors("file")).isTrue
    }

    data class Form(@field:FileNotEmpty
                    private val file: MultipartFile?)
}