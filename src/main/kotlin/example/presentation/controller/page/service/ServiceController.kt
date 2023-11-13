package example.presentation.controller.page.service

import example.presentation.shared.session.*
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/service")
class ServiceController {
    /**
     * 利用規約画面を表示する
     */
    @GetMapping("/agreement")
    fun displayAgreementPage(): String {
        return "service/agreement"
    }
}