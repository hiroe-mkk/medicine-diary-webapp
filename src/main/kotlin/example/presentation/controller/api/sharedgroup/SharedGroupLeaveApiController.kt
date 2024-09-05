package example.presentation.controller.api.sharedgroup

import example.application.service.sharedgroup.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/shared-group/leave")
class SharedGroupLeaveApiController(private val sharedGroupService: SharedGroupService,
                                    private val userSessionProvider: UserSessionProvider) {
    /**
     * 共有グループから脱退する
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun leaveSharedGroup() {
        sharedGroupService.leaveSharedGroup(userSessionProvider.getUserSessionOrElseThrow())
    }
}
