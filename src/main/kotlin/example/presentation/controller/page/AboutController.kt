package example.presentation.controller.page

import example.presentation.shared.session.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/about")
class AboutController {
    /**
     * アバウト画面を表示する
     */
    @GetMapping
    fun displayAboutPage(): String {
        return "about"
    }
}