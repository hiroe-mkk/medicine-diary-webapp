package example.presentation.controller.page.sharedgroup

import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/shared-group/invite")
class SharedGroupInvitePageController {
    /**
     * 共有グループ招待画面を表示する
     */
    @GetMapping
    fun displaySharedGroupInvitePage(): String {
        return "sharedgroup/invite"
    }
}
