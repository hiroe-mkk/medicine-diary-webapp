package example.domain.model.sharedgroup

import java.time.*

/**
 * 招待中
 */
data class PendingInvitation(val inviteCode: String,
                             val invitedOn: LocalDate) {
    fun isValid(date: LocalDate): Boolean {
        val expiredOn = invitedOn.plusDays(8)
        return date.isBefore(expiredOn)
    }
}
