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
@RequestMapping("/sharedgroup/unshare")
class UnshareController(private val sharedGroupService: SharedGroupService,
                        private val userSessionProvider: UserSessionProvider) {
    /**
     * 共有を解除する
     */
    @PostMapping
    fun unshare(@ModelAttribute sharedGroupId: SharedGroupId,
                redirectAttributes: RedirectAttributes): String {
        sharedGroupService.unshare(sharedGroupId, userSessionProvider.getUserSession())
        val resultMessage = ResultMessage.info("共有を解除しました。")
        redirectAttributes.addFlashAttribute("resultMessage", resultMessage)
        return "redirect:/sharedgroup/management"
    }
}