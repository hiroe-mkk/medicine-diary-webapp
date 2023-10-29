package example.presentation.controller.page.sharedgroup

import example.application.service.sharedgroup.*
import example.domain.model.sharedgroup.*
import example.domain.shared.message.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.*

@Controller
@RequestMapping("/sharedgroup/decline")
class DeclineInvitationToSharedGroupController(private val sharedGroupService: SharedGroupService,
                                               private val userSessionProvider: UserSessionProvider) {
    /**
     * 共有グループへの招待を拒否する
     */
    @PostMapping
    fun declineInvitationToSharedGroup(@ModelAttribute sharedGroupId: SharedGroupId,
                                       redirectAttributes: RedirectAttributes): String {
        val resultMessage = try {
            sharedGroupService.declineInvitationToSharedGroup(sharedGroupId, userSessionProvider.getUserSession())
            ResultMessage.info("共有グループへの招待を拒否しました。")
        } catch (ex: ParticipationInSharedGroupException) {
            ex.resultMessage
        }
        redirectAttributes.addFlashAttribute("resultMessage", resultMessage)
        return "redirect:/sharedgroup/management"
    }
}