package example.presentation.controller.api.sharedgroup

import example.application.service.sharedgroup.*
import example.domain.model.account.*
import example.domain.model.sharedgroup.*
import example.domain.shared.message.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.*

@Controller
@RequestMapping("/api/shared-group/participate")
class ParticipationInSharedGroupApiController(private val sharedGroupService: SharedGroupService,
                                              private val userSessionProvider: UserSessionProvider) {
    /**
     * 共有グループに参加する
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun participateInSharedGroup(sharedGroupId: SharedGroupId) {
        sharedGroupService.participateInSharedGroup(sharedGroupId, userSessionProvider.getUserSessionOrElseThrow())
    }
}