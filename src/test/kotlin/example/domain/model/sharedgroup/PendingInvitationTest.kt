package example.domain.model.sharedgroup

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.params.*
import org.junit.jupiter.params.provider.*
import java.time.*

class PendingInvitationTest {
    private val pendingInvitation = PendingInvitation("inviteCode", LocalDate.of(2020, 1, 1))

    @ParameterizedTest
    @CsvSource("7, true",
               "8, true",
               "9, false")
    fun canDetectValidInviteCode(dayOfMonth: Int, result: Boolean) {
        //when:
        val actual = pendingInvitation.isValid(LocalDate.of(2020, 1, dayOfMonth))

        //then:
        assertThat(actual).isEqualTo(result)
    }
}
