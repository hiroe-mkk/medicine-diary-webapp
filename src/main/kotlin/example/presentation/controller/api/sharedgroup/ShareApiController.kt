package example.presentation.controller.api.sharedgroup

import example.application.service.sharedgroup.*
import example.domain.model.account.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/shared-group/share")
class ShareApiController(private val sharedGroupService: SharedGroupService,
                         private val userSessionProvider: UserSessionProvider) {
    /**
     * 共有する
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun share(accountId: AccountId) {
        sharedGroupService.share(accountId, userSessionProvider.getUserSessionOrElseThrow())
    }
}