package example.presentation.controller.page.sharedgroup

import example.application.service.sharedgroup.*
import example.domain.model.account.*
import example.domain.model.sharedgroup.*
import example.domain.shared.message.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.*

@Controller
@RequestMapping("/sharedgroup/cancel")
class CancelInvitationToSharedGroupController(private val sharedGroupService: SharedGroupService,
                                              private val userSessionProvider: UserSessionProvider) {
    /**
     * 共有グループへの招待をキャンセルする
     */
    @PostMapping
    fun cancelInvitationToSharedGroup(@ModelAttribute sharedGroupId: SharedGroupId,
                                      @ModelAttribute accountId: AccountId,
                                      redirectAttributes: RedirectAttributes): String {
        sharedGroupService.cancelInvitationToSharedGroup(sharedGroupId, accountId, userSessionProvider.getUserSession())
        val resultMessage = ResultMessage.info("共有グループへの招待をキャンセルしました。")
        redirectAttributes.addFlashAttribute("resultMessage", resultMessage)
        return "redirect:/sharedgroup/management"
    }
}