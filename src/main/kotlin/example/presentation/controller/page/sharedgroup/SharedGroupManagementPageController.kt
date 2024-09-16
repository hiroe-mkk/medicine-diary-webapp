package example.presentation.controller.page.sharedgroup

import example.application.service.sharedgroup.*
import example.presentation.shared.session.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/shared-group")
class SharedGroupManagementPageController(private val sharedGroupQueryService: SharedGroupQueryService,
                                          private val userSessionProvider: UserSessionProvider,
                                          private val lastRequestedPage: LastRequestedPage) {
    /**
     * 共有グループ管理画面を表示する
     */
    @GetMapping
    fun displaySharedGroupManagementPage(model: Model): String {
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        model.addAttribute("joinedSharedGroupId", sharedGroupQueryService.getJoinedSharedGroup(userSession))

        lastRequestedPage.path = "/shared-group"
        return "sharedgroup/management"
    }
}
