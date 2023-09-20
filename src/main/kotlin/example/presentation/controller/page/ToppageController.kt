package example.presentation.controller.page

import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/")
class ToppageController {
    /**
     * トップページ画面を表示する
     */
    @GetMapping
    fun displayToppagePage(): String {
        return "toppage"
    }
}