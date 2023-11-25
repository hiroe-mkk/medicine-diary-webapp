package example.presentation.controller.page

import example.presentation.shared.session.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/agreement")
class AgreementPageController {
    /**
     * 利用規約画面を表示する
     */
    @GetMapping()
    fun displayAgreementPage(): String {
        return "agreement"
    }
}