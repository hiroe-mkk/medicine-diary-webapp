package example.presentation.controller.page

import example.application.service.contact.*
import example.domain.model.contact.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/contact")
class ContactPageController {
    /**
     * お問い合わせ画面を表示する
     */
    @GetMapping
    fun displayContactPage(model: Model): String {
        model.addAttribute("form", ContactFormCreationCommand.initialize())
        return "contact/form"
    }
}