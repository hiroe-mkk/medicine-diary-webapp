package example.application.service.sharedgroup

import example.application.shared.usersession.*
import example.domain.model.sharedgroup.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class SharedGroupLeaveService(private val sharedGroupLeaveCoordinator: SharedGroupLeaveCoordinator) {
    /**
     * 共有グループから脱退する
     */
    fun leaveSharedGroup(userSession: UserSession) {
        return sharedGroupLeaveCoordinator.leaveSharedGroupAndCloneMedicines(userSession.accountId)
    }
}
