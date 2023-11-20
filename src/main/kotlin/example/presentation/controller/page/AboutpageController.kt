package example.presentation.controller.page

import example.presentation.shared.session.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/about")
class AboutpageController {
    /**
     * アバウトページ画面を表示する
     */
    @GetMapping
    fun displayAboutpagePage(): String {
        return "aboutpage"
    }
}