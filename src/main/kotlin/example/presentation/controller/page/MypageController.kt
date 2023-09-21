package example.presentation.controller.page

import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/mypage")
class MypageController {
    /**
     * マイページ画面を表示する
     */
    @GetMapping
    fun displayMypagePage(): String = "mypage"
}