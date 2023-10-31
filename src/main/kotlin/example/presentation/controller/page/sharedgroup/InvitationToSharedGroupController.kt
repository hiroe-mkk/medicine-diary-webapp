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
@RequestMapping("/sharedgroup/invite")
class InvitationToSharedGroupController(private val sharedGroupService: SharedGroupService,
                                        private val userSessionProvider: UserSessionProvider) {
    /**
     * 共有グループに招待する
     */
    @PostMapping
    fun inviteToSharedGroup(@ModelAttribute sharedGroupId: SharedGroupId,
                            @ModelAttribute accountId: AccountId,
                            redirectAttributes: RedirectAttributes): String {
        val resultMessage = try {
            sharedGroupService.inviteToSharedGroup(sharedGroupId, accountId, userSessionProvider.getUserSession())
            ResultMessage.info("共有グループに招待しました。")
        } catch (ex: InvitationToSharedGroupException) {
            ex.resultMessage
        }
        redirectAttributes.addFlashAttribute("resultMessage", resultMessage)
        return "redirect:/sharedgroup/management"
    }
}