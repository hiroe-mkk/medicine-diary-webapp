package example.presentation.controller.page

import example.presentation.shared.*
import example.presentation.shared.session.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/mypage")
@SessionAttributes(types = [LastRequestedPagePath::class])
class MypageController {
    @ModelAttribute("lastRequestedPagePath")
    fun lastRequestedPagePath(): LastRequestedPagePath = LastRequestedPagePath("/mypage")

    /**
     * マイページ画面を表示する
     */
    @GetMapping
    fun displayMypagePage(): String = "mypage"
}