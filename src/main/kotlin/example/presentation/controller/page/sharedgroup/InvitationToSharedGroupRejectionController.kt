package example.presentation.controller.page.sharedgroup

import example.application.service.sharedgroup.*
import example.domain.model.sharedgroup.*
import example.domain.shared.message.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.*

@Controller
@RequestMapping("/shared-group/reject")
class InvitationToSharedGroupRejectionController(private val sharedGroupService: SharedGroupService,
                                                 private val userSessionProvider: UserSessionProvider) {
    /**
     * 共有グループへの招待を拒否する
     */
    @PostMapping
    fun rejectInvitationToSharedGroup(@ModelAttribute sharedGroupId: SharedGroupId,
                                      redirectAttributes: RedirectAttributes): String {
        sharedGroupService.rejectInvitationToSharedGroup(sharedGroupId, userSessionProvider.getUserSessionOrElseThrow())
        val resultMessage = ResultMessage.info("共有グループへの招待を拒否しました。")
        redirectAttributes.addFlashAttribute("resultMessage", resultMessage)
        return "redirect:/shared-group/management"
    }
}