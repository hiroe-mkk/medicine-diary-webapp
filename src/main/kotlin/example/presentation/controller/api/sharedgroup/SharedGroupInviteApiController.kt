package example.presentation.controller.api.sharedgroup

import example.application.service.sharedgroup.*
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
    fun inviteToSharedGroup(sharedGroupInviteFormCommand: SharedGroupInviteFormCommand) {
        sharedGroupService.inviteToSharedGroup(sharedGroupInviteFormCommand,
                                               userSessionProvider.getUserSessionOrElseThrow())
    }
}
