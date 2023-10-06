package example.presentation.controller.page.medicine

import example.application.service.medicine.*
import example.domain.model.medicine.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/medicines/register")
class MedicineRegistrationController {
    /**
     * 薬登録画面を表示する
     */
    @GetMapping
    fun displayMedicineRegistrationPage(model: Model): String {
        model.addAttribute("form", MedicineBasicInfoInputCommand.initialize())
        model.addAttribute("title", "お薬登録")
        model.addAttribute("timings", Timing.values())
        model.addAttribute("executePath", "/medicines/register")
        return "medicine/form"
    }
}