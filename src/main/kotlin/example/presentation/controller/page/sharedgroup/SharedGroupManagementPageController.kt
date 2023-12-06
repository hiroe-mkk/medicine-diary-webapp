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
class SharedGroupManagementPageController {
    /**
     * 共有グループ管理画面を表示する
     */
    @GetMapping
    fun displaySharedGroupManagementPage(model: Model): String {
        model.addAttribute("lastRequestedPagePath", "/shared-group/management")
        return "sharedgroup/management"
    }
}