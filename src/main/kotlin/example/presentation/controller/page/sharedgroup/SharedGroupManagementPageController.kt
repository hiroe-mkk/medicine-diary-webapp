package example.presentation.controller.page.sharedgroup

import example.application.query.sharedgroup.*
import example.presentation.shared.session.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/shared-group/management")
@SessionAttributes(value = ["lastRequestedPagePath"])
class SharedGroupManagementPageController(private val jsonSharedGroupQueryService: JSONSharedGroupQueryService,
                                          private val userSessionProvider: UserSessionProvider) {
    /**
     * 共有グループ管理画面を表示する
     */
    @GetMapping
    fun displaySharedGroupManagementPage(model: Model): String {
        val (participatingSharedGroup, invitedSharedGroups) =
                jsonSharedGroupQueryService.findJSONSharedGroup(userSessionProvider.getUserSessionOrElseThrow())
        model.addAttribute("participatingSharedGroup", participatingSharedGroup)
        model.addAttribute("invitedSharedGroups", invitedSharedGroups)

        model.addAttribute("lastRequestedPagePath", "/shared-group/management")
        return "sharedgroup/management"
    }
}