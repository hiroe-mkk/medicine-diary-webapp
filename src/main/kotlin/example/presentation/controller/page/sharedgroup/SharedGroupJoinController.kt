package example.presentation.controller.page.sharedgroup

import example.application.service.sharedgroup.*
import example.domain.shared.message.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.*

@Controller
@RequestMapping("/shared-group/join")
class SharedGroupJoinController(private val sharedGroupInviteResponseService: SharedGroupInviteResponseService,
                                private val userSessionProvider: UserSessionProvider) {
    /**
     * 共有グループに参加する
     */
    @PostMapping
    fun joinSharedGroup(code: String, redirectAttributes: RedirectAttributes): String {
        sharedGroupInviteResponseService.joinSharedGroup(code, userSessionProvider.getUserSessionOrElseThrow())

        redirectAttributes.addFlashAttribute("resultMessage",
                                             ResultMessage.info("共有グループに参加しました。"))
        return "redirect:/shared-group"
    }
}
