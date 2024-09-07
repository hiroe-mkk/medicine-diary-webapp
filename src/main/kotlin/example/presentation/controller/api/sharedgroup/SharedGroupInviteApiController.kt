package example.presentation.controller.api.sharedgroup

import example.application.service.sharedgroup.*
import example.domain.model.sharedgroup.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/shared-group/invite")
class SharedGroupInviteApiController(private val sharedGroupService: SharedGroupService,
                                     private val userSessionProvider: UserSessionProvider) {
    /**
     * 共有グループに招待する
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun inviteToSharedGroup(sharedGroupId: SharedGroupId, sharedGroupInviteFormCommand: SharedGroupInviteFormCommand) {
        sharedGroupService.inviteToSharedGroup(sharedGroupId,
                                               sharedGroupInviteFormCommand,
                                               userSessionProvider.getUserSessionOrElseThrow())
    }
}
