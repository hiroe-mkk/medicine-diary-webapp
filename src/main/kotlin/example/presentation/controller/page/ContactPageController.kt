package example.presentation.controller.page

import example.application.service.contact.*
import example.application.service.medicine.*
import example.domain.model.contact.*
import example.domain.shared.message.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.validation.*
import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.*

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

    /**
     * お問い合わせ内容を確認する
     */
    @PostMapping(params = ["confirm"])
    fun confirmContactContent(@ModelAttribute("form") @Validated contactFormCreationCommand: ContactFormCreationCommand,
                         bindingResult: BindingResult): String {
        if (bindingResult.hasErrors()) return "contact/form"

        return "contact/confirm"
    }
}