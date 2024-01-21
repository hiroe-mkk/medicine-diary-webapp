package example.presentation.controller.page

import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/notice")
class NoticePageController {
    /**
     * お知らせ画面を表示する
     */
    @GetMapping
    fun displayNoticePage(): String {
        return "notice"
    }
}