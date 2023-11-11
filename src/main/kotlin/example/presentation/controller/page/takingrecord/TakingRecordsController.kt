package example.presentation.controller.page.takingrecord

import example.presentation.shared.session.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/takingrecords")
@SessionAttributes(types = [LastRequestedPagePath::class])
class TakingRecordsController {
    @ModelAttribute("lastRequestedPagePath")
    fun lastRequestedPagePath(): LastRequestedPagePath = LastRequestedPagePath("/medicines")

    /**
     * 服用記録一覧画面を表示する
     */
    @GetMapping
    fun displayTakingRecordsPage(model: Model): String {
        return "takingrecord/list"
    }
}