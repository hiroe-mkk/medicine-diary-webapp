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
@RequestMapping("/sharedgroup/participate")
class ParticipationInSharedGroupController(private val sharedGroupService: SharedGroupService,
                                           private val userSessionProvider: UserSessionProvider) {
    /**
     * 共有グループに参加する
     */
    @PostMapping
    fun participateInSharedGroup(@ModelAttribute sharedGroupId: SharedGroupId,
                                 redirectAttributes: RedirectAttributes): String {
        val resultMessage = try {
            sharedGroupService.participateInSharedGroup(sharedGroupId, userSessionProvider.getUserSession())
            ResultMessage.info("共有グループに参加しました。")
        } catch (ex: ParticipationInSharedGroupException) {
            ex.resultMessage
        }
        redirectAttributes.addFlashAttribute("resultMessage", resultMessage)
        return "redirect:/sharedgroup/management"
    }
}