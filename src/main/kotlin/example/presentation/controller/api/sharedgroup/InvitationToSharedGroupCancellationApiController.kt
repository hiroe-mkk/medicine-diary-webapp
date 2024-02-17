package example.presentation.controller.api.sharedgroup

import example.application.service.sharedgroup.*
import example.domain.model.account.*
import example.domain.model.sharedgroup.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/shared-group/cancel")
class InvitationToSharedGroupCancellationApiController(private val sharedGroupService: SharedGroupService,
                                                       private val userSessionProvider: UserSessionProvider) {
    /**
     * 共有グループへの招待を取り消す
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun cancelInvitationToSharedGroup(sharedGroupId: SharedGroupId,
                                      accountId: AccountId) {
        sharedGroupService.cancelInvitationToSharedGroup(sharedGroupId,
                                                         accountId,
                                                         userSessionProvider.getUserSessionOrElseThrow())
    }
}