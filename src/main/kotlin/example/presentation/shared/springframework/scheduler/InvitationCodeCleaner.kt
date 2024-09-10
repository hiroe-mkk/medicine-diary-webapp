package example.presentation.shared.springframework.scheduler

import example.application.service.sharedgroup.*
import org.springframework.scheduling.annotation.*
import org.springframework.stereotype.*

@Component
class InvitationCodeCleaner(private val sharedGroupService: SharedGroupService) {
    @Scheduled(cron = "0 0 0 * * 3") // 毎週水曜日の0時に実行
    fun deleteExpiredInvitationCodes() {
        sharedGroupService.deleteExpiredPendingInvitation()
    }
}
