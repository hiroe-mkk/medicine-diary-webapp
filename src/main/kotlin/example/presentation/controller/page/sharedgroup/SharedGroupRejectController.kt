package example.presentation.controller.page.sharedgroup

import example.application.service.sharedgroup.*
import example.domain.shared.message.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.*

@Controller
@RequestMapping("/shared-group/reject")
class SharedGroupRejectController(private val sharedGroupService: SharedGroupService,
                                  private val userSessionProvider: UserSessionProvider) {
    /**
     * 共有グループへの招待を拒否する
     */
    @PostMapping
    fun rejectInvitationToSharedGroup(code: String, redirectAttributes: RedirectAttributes): String {
        sharedGroupService.rejectInvitationToSharedGroup(code, userSessionProvider.getUserSessionOrElseThrow())

        redirectAttributes.addFlashAttribute("resultMessage",
                                             ResultMessage.info("共有グループへの参加を拒否しました。"))
        return "redirect:/shared-group"
    }
}
