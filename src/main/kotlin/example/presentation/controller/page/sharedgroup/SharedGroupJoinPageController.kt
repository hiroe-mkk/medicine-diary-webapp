package example.presentation.controller.page.sharedgroup

import example.application.service.sharedgroup.*
import example.domain.model.sharedgroup.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/shared-group/join")
class SharedGroupJoinPageController(private val sharedGroupQueryService: SharedGroupQueryService,
                                    private val userSessionProvider: UserSessionProvider) {
    /**
     * 共有グループ参加画面を表示する
     */
    @GetMapping(params = ["code"])
    fun displaySharedGroupJoinPage(code: String, model: Model): String {
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        val joinedSharedGroupId = sharedGroupQueryService.getJoinedSharedGroup(userSession)
        model.addAttribute("joinedSharedGroupId", joinedSharedGroupId)

        try {
            val invitedSharedGroupId = sharedGroupQueryService.getInvitedSharedGroupId(code, userSession)
            model.addAttribute("inviteCode", code)
            model.addAttribute("invitedSharedGroupId", invitedSharedGroupId)
        } catch (ex: InvalidInvitationException) {
            model.addAttribute("errorMessage", ex.resultMessage)
        }

        return "sharedgroup/join"
    }
}
