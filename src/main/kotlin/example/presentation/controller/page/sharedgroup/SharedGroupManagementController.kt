package example.presentation.controller.page.sharedgroup

import example.application.query.sharedgroup.*
import example.presentation.shared.session.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/sharedgroup/management")
@SessionAttributes(types = [LastRequestedPagePath::class])
class SharedGroupManagementController(private val JSONSharedGroupQueryService: JSONSharedGroupQueryService,
                                      private val userSessionProvider: UserSessionProvider) {
    @ModelAttribute("lastRequestedPagePath")
    fun lastRequestedPagePath(): LastRequestedPagePath = LastRequestedPagePath("/sharedgroup/management")

    /**
     * 共有グループ管理画面を表示する
     */
    @GetMapping
    fun displaySharedGroupManagementPage(model: Model): String {
        val (participatingSharedGroup, invitedSharedGroups) =
                JSONSharedGroupQueryService.findSharedGroup(userSessionProvider.getUserSession())
        model.addAttribute("participatingSharedGroup", participatingSharedGroup)
        model.addAttribute("invitedSharedGroups", invitedSharedGroups)
        return "sharedgroup/management"
    }
}