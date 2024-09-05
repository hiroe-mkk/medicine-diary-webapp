package example.presentation.controller.page.medicine

import example.application.service.medicine.*
import example.application.service.sharedgroup.*
import example.domain.model.medicine.*
import example.domain.shared.message.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.validation.*
import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.*

@Controller
@RequestMapping("/medicines/register")
class MedicineRegistrationController(private val medicineService: MedicineService,
                                     private val sharedGroupService: SharedGroupService,
                                     private val userSessionProvider: UserSessionProvider) {
    @ModelAttribute("timings")
    fun timings(): Array<Timing> = Timing.values()

    @ModelAttribute("isJoinedSharedGroup")
    fun isJoinedSharedGroup(): Boolean {
        return sharedGroupService.isJoinedSharedGroup(userSessionProvider.getUserSessionOrElseThrow())
    }

    @ModelAttribute("title")
    fun title(): String = "お薬登録"

    @ModelAttribute("executePath")
    fun executePath(): String = "/medicines/register"

    /**
     * 薬登録画面を表示する
     */
    @GetMapping
    fun displayMedicineRegistrationPage(model: Model): String {
        val form =
                medicineService.getRegistrationMedicineBasicInfoEditCommand(userSessionProvider.getUserSessionOrElseThrow())
        model.addAttribute("form", form)
        return "medicine/form"
    }

    /**
     * 薬を登録する
     */
    @PostMapping
    fun registerMedicine(@ModelAttribute("form") @Validated medicineBasicInfoEditCommand: MedicineBasicInfoEditCommand,
                         bindingResult: BindingResult,
                         redirectAttributes: RedirectAttributes): String {
        if (bindingResult.hasErrors()) return "medicine/form"

        val medicineId = medicineService.registerMedicine(medicineBasicInfoEditCommand,
                                                          userSessionProvider.getUserSessionOrElseThrow())
        redirectAttributes.addFlashAttribute("resultMessage",
                                             ResultMessage.info("お薬の登録が完了しました。"))
        redirectAttributes.addAttribute("medicineId", medicineId)
        return "redirect:/medicines/{medicineId}"
    }
}
