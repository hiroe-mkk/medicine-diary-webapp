package example.presentation.controller.page

import example.presentation.shared.*
import example.presentation.shared.session.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/")
@SessionAttributes(types = [LastRequestedPagePath::class])
class ToppageController {
    @ModelAttribute("lastRequestedPagePath")
    fun lastRequestedPagePath(): LastRequestedPagePath = LastRequestedPagePath("/")

    /**
     * トップページ画面を表示する
     */
    @GetMapping
    fun displayToppagePage(): String {
        return "toppage"
    }
}