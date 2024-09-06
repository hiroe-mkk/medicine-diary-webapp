package example.presentation.controller.api.sharedgroup

import example.application.service.sharedgroup.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/shared-group/reject")
class SharedGroupRejectApiController(private val sharedGroupService: SharedGroupService,
                                     private val userSessionProvider: UserSessionProvider) {
    /**
     * 共有グループへの招待を拒否する
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun rejectInvitationToSharedGroup(code: String) {
        sharedGroupService.rejectInvitationToSharedGroup(code, userSessionProvider.getUserSessionOrElseThrow())
    }
}
