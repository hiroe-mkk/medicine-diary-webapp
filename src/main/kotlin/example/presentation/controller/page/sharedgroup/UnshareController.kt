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
@RequestMapping("/shared-group/unshare")
class UnshareController(private val sharedGroupService: SharedGroupService,
                        private val userSessionProvider: UserSessionProvider) {
    /**
     * 共有を停止する
     */
    @PostMapping
    fun unshare(redirectAttributes: RedirectAttributes): String {
        sharedGroupService.unshare(userSessionProvider.getUserSessionOrElseThrow())
        val resultMessage = ResultMessage.info("共有を停止しました。")
        redirectAttributes.addFlashAttribute("resultMessage", resultMessage)
        return "redirect:/shared-group/management"
    }
}