package example.presentation.controller.page.sharedgroup

import example.application.service.sharedgroup.*
import example.domain.model.account.*
import example.domain.model.sharedgroup.*
import example.domain.shared.message.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.*

@Controller
@RequestMapping("/shared-group/share")
class ShareController(private val sharedGroupService: SharedGroupService,
                      private val userSessionProvider: UserSessionProvider) {
    /**
     * 共有する
     */
    @PostMapping
    fun share(@ModelAttribute accountId: AccountId,
              redirectAttributes: RedirectAttributes): String {
        val resultMessage = try {
            sharedGroupService.share(accountId, userSessionProvider.getUserSession())
            ResultMessage.info("共有リクエストを送信しました。")
        } catch (ex: ShareException) {
            ex.resultMessage
        }
        redirectAttributes.addFlashAttribute("resultMessage", resultMessage)
        return "redirect:/sharedgroup/management"
    }
}