package example.presentation.controller.api.sharedgroup

import example.application.service.sharedgroup.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/shared-group/join")
class SharedGroupJoinApiController(private val sharedGroupService: SharedGroupService,
                                   private val userSessionProvider: UserSessionProvider) {
    /**
     * 共有グループに参加する
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun joinSharedGroup(code: String) {
        sharedGroupService.joinSharedGroup(code, userSessionProvider.getUserSessionOrElseThrow())
    }
}
