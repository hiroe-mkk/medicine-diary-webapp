package example.domain.model.medicine

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.params.*
import org.junit.jupiter.params.provider.*

internal class DosageTest {
    @ParameterizedTest
    @CsvSource("1.0, 1",
               "1.00, 1",
               "1.1, 1.1",
               "1.10, 1.1")
    fun canConvertToString(quantity: Double, expected: String) {
        //given:
        val dosage = Dosage(quantity)

        //when:
        val actual = dosage.toString()

        //then:
        assertThat(actual).isEqualTo(expected)
    }
}