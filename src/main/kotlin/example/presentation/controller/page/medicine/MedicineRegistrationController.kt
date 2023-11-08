package example.presentation.controller.page.medicine

import example.application.service.medicine.*
import example.application.service.sharedgroup.*
import example.application.shared.usersession.*
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

    @ModelAttribute("isParticipatingInSharedGroup")
    fun isParticipatingInSharedGroup(): Boolean {
        return sharedGroupService.isParticipatingInSharedGroup(userSessionProvider.getUserSession())
    }

    /**
     * 薬登録画面を表示する
     */
    @GetMapping
    fun displayMedicineRegistrationPage(model: Model): String {
        model.addAttribute("form", MedicineBasicInfoEditCommand.initialize())
        return "medicine/registrationForm"
    }

    /**
     * 薬を登録する
     */
    @PostMapping
    fun registerMedicine(@ModelAttribute("form") @Validated medicineBasicInfoEditCommand: MedicineBasicInfoEditCommand,
                         bindingResult: BindingResult,
                         redirectAttributes: RedirectAttributes): String {
        if (bindingResult.hasErrors()) return "medicine/basicInfoForm"

        medicineService.registerMedicine(medicineBasicInfoEditCommand,
                                         userSessionProvider.getUserSession())
        redirectAttributes.addFlashAttribute("resultMessage",
                                             ResultMessage.info("お薬の登録が完了しました。"))
        return "redirect:/medicines"
    }
}