package example.domain.model.sharedgroup

import java.time.*

/**
 * 招待中
 */
data class PendingInvitation(val inviteCode: String,
                             val invitedOn: LocalDate) {
    companion object {
        const val EXPIRATION_DAYS = 7
        const val EXPIRATION_PERIOD_TEXT = "1週間"
    }

    fun isValid(date: LocalDate): Boolean {
        return date.isBefore(invitedOn.plusDays(EXPIRATION_DAYS + 1L))
    }
}
