package example.presentation.controller.page.medicine

import example.application.service.medicine.*
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
@RequestMapping("/medicines/{medicineId}/update")
class MedicineUpdateController(private val medicineService: MedicineService,
                               private val userSessionProvider: UserSessionProvider) {
    @ModelAttribute("timings")
    fun timings(): Array<Timing> = Timing.values()

    @ModelAttribute("title")
    fun title(): String = "お薬更新"

    @ModelAttribute("executePath")
    fun executePath(@PathVariable medicineId: MedicineId): String = "/medicines/${medicineId}/update"

    @ModelAttribute("medicineId")
    fun medicineId(@PathVariable medicineId: MedicineId): MedicineId = medicineId

    /**
     * 薬更新画面を表示する
     */
    @GetMapping
    fun displayMedicineUpdatePage(@PathVariable medicineId: MedicineId,
                                  model: Model): String {
        val command = medicineService.getInitializedMedicineBasicInfoInputCommand(medicineId,
                                                                                  userSessionProvider.getUserSession())
        model.addAttribute("form", command)
        return "medicine/form"
    }

    /**
     * 薬を更新する
     */
    @PostMapping
    fun updateMedicine(@PathVariable medicineId: MedicineId,
                       @ModelAttribute("form") @Validated medicineBasicInfoInputCommand: MedicineBasicInfoInputCommand,
                       bindingResult: BindingResult,
                       redirectAttributes: RedirectAttributes): String {
        if (bindingResult.hasErrors()) return "medicine/form"

        medicineService.updateMedicineBasicInfo(medicineId,
                                                medicineBasicInfoInputCommand,
                                                userSessionProvider.getUserSession())
        redirectAttributes.addFlashAttribute("resultMessage",
                                             ResultMessage.info("薬の更新が完了しました。"))
        redirectAttributes.addAttribute("medicineId", medicineId)
        return "redirect:/medicines/{medicineId}"
    }
}