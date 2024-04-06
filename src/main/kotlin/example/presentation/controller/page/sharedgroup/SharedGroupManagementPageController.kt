package example.presentation.controller.page.sharedgroup

import example.presentation.shared.session.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/shared-group/management")
class SharedGroupManagementPageController(private val lastRequestedPage: LastRequestedPage) {
    /**
     * 共有グループ管理画面を表示する
     */
    @GetMapping
    fun displaySharedGroupManagementPage(model: Model): String {
        lastRequestedPage.path = "/shared-group/management"
        return "sharedgroup/management"
    }
}
