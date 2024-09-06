package example.presentation.controller.api.sharedgroup

import example.application.service.sharedgroup.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/shared-group/create")
class SharedGroupCreationApiController(private val sharedGroupService: SharedGroupService,
                                       private val userSessionProvider: UserSessionProvider) {
    /**
     * 共有グループを作る
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun createSharedGroup() {
        sharedGroupService.createSharedGroup(userSessionProvider.getUserSessionOrElseThrow())
    }
}
