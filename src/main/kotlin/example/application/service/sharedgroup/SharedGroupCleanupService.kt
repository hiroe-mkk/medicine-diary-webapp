package example.application.service.sharedgroup

import example.domain.model.sharedgroup.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class SharedGroupCleanupService(private val sharedGroupRepository: SharedGroupRepository) {
    /**
     * 有効期限が切れた招待を削除する
     */
    fun deleteExpiredPendingInvitation() {
        sharedGroupRepository.deleteExpiredPendingInvitation()
    }
}
