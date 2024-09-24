package example.presentation.controller.api.sharedgroup

import example.application.service.sharedgroup.*
import example.presentation.shared.usersession.*
import jakarta.validation.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/shared-group/invite")
class SharedGroupInviteApiController(private val sharedGroupInviteService: SharedGroupInviteService,
                                     private val userSessionProvider: UserSessionProvider) {
    /**
     * 共有グループに招待する
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun inviteToSharedGroup(@Valid sharedGroupInviteFormCommand: SharedGroupInviteFormCommand) {
        sharedGroupInviteService.inviteToSharedGroup(sharedGroupInviteFormCommand,
                                                     userSessionProvider.getUserSessionOrElseThrow())
    }
}
