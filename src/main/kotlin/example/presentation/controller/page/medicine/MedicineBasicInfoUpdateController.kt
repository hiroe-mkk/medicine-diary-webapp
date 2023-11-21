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
@RequestMapping("/medicines/{medicineId}/basicinfo/update")
class MedicineBasicInfoUpdateController(private val medicineService: MedicineService,
                                        private val userSessionProvider: UserSessionProvider) {
    @ModelAttribute("medicineId")
    fun medicineId(@PathVariable medicineId: MedicineId): MedicineId = medicineId

    @ModelAttribute("timings")
    fun timings(): Array<Timing> = Timing.values()

    @ModelAttribute("isOwned")
    fun isOwned(@PathVariable medicineId: MedicineId): Boolean {
        return medicineService.isOwnedMedicine(medicineId, userSessionProvider.getUserSessionOrElseThrow())
    }

    /**
     * 薬基本情報更新画面を表示する
     */
    @GetMapping
    fun displayMedicineBasicInfoUpdatePage(@PathVariable medicineId: MedicineId,
                                           model: Model): String {
        val command = medicineService.getUpdateMedicineBasicInfoEditCommand(medicineId,
                                                                            userSessionProvider.getUserSessionOrElseThrow())
        model.addAttribute("form", command)
        return "medicine/updateForm"
    }

    /**
     * 薬基本情報を更新する
     */
    @PostMapping
    fun updateMedicineBasicInfo(@PathVariable medicineId: MedicineId,
                                @ModelAttribute("form") @Validated medicineBasicInfoEditCommand: MedicineBasicInfoEditCommand,
                                bindingResult: BindingResult,
                                redirectAttributes: RedirectAttributes): String {
        if (bindingResult.hasErrors()) return "medicine/updateForm"

        medicineService.updateMedicineBasicInfo(medicineId,
                                                medicineBasicInfoEditCommand,
                                                userSessionProvider.getUserSessionOrElseThrow())
        redirectAttributes.addFlashAttribute("resultMessage",
                                             ResultMessage.info("お薬基本情報の更新が完了しました。"))
        redirectAttributes.addAttribute("medicineId", medicineId)
        return "redirect:/medicines/{medicineId}"
    }
}