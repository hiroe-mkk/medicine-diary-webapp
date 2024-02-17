package example.presentation.controller.page

import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/about")
class AboutPageController {
    /**
     * アバウト画面を表示する
     */
    @GetMapping
    fun displayAboutPage(): String {
        return "about"
    }
}