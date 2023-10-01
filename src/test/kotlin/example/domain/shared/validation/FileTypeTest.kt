package example.domain.shared.validation

import example.testhelper.factory.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.junit.jupiter.params.*
import org.junit.jupiter.params.provider.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.autoconfigure.validation.*
import org.springframework.http.*
import org.springframework.test.context.junit.jupiter.*
import org.springframework.validation.*
import org.springframework.web.multipart.*

@SpringJUnitConfig(classes = [ValidationAutoConfiguration::class])
internal class FileTypeTest(@Autowired val validator: Validator) {
    @ParameterizedTest
    @CsvSource("image/jpeg, false",
               "image/png, true",
               "application/pdf, true")
    fun canDetectInvalidMultipartFile(mediaType: String, result: Boolean) {
        //given:
        val file = TestProfileImageFactory.createMultipartFile(type = MediaType.parseMediaType(mediaType))
        val form = Form(file)
        val bindException = BindException(form, "form")

        //when:
        validator.validate(form, bindException)

        //then:
        val bindingResult = bindException.bindingResult
        assertThat(bindingResult.hasFieldErrors("file")).isEqualTo(result)
    }

    @Test
    fun nullIsValidMultipartFile() {
        //given:
        val form = Form(null)
        val bindException = BindException(form, "form")

        //when:
        validator.validate(form, bindException)

        //then:
        val bindingResult = bindException.bindingResult
        assertThat(bindingResult.hasFieldErrors("file")).isFalse
    }

    data class Form(@field:FileType([MediaType.IMAGE_JPEG_VALUE])
                    val file: MultipartFile?)
}