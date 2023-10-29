import example.domain.shared.validation.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.junit.jupiter.params.*
import org.junit.jupiter.params.provider.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.autoconfigure.validation.*
import org.springframework.test.context.junit.jupiter.*
import org.springframework.validation.*

@SpringJUnitConfig(classes = [ValidationAutoConfiguration::class])
internal class NotWhitespaceOnlyTest(@Autowired val validator: Validator) {
    @ParameterizedTest
    @ValueSource(strings = [
        "a",
        " a",
        "a ",
    ])
    fun shouldBeValid(value: String) {
        //given:
        val testData = TestData(value)
        val bindException = BindException(testData, "testData")

        //when:
        validator.validate(testData, bindException)

        //then:
        val bindingResult = bindException.bindingResult
        assertThat(bindingResult.hasFieldErrors("value")).isFalse
    }

    @Test
    fun nullIsValid() {
        //given:
        val testData = TestData(null)
        val bindException = BindException(testData, "testData")

        //when:
        validator.validate(testData, bindException)

        //then:
        val bindingResult = bindException.bindingResult
        assertThat(bindingResult.hasFieldErrors("value")).isFalse
    }

    @ParameterizedTest
    @ValueSource(strings = [
        "",
        " ", // 半角スペース
        "  ", // 半角スペース2個
        "　", // 全角スペース
        "\n",
        "\t",
        "\r"
    ])
    fun whitespaceIsInvalid(value: String) {
        //given:
        val testData = TestData(value)
        val bindException = BindException(testData, "testData")

        //when:
        validator.validate(testData, bindException)

        //then:
        val bindingResult = bindException.bindingResult
        assertThat(bindingResult.hasFieldErrors("value")).isTrue
    }

    data class TestData(@NotWhitespaceOnly
                        val value: String?)
}