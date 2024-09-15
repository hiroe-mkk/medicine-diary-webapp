package example.presentation.controller.page.medicine

import example.application.service.medicine.*
import example.application.service.sharedgroup.*
import example.domain.model.medicine.*
import example.domain.shared.exception.*
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
class MedicineBasicInfoUpdateController(private val medicineQueryService: MedicineQueryService,
                                        private val medicineUpdateService: MedicineUpdateService,
                                        private val sharedGroupService: SharedGroupService,
                                        private val userSessionProvider: UserSessionProvider) {
    @ModelAttribute("timings")
    fun timings(): Array<Timing> = Timing.entries.toTypedArray()

    @ModelAttribute("isJoinedSharedGroup")
    fun isJoinedSharedGroup(): Boolean {
        return sharedGroupService.isJoinedSharedGroup(userSessionProvider.getUserSessionOrElseThrow())
    }

    @ModelAttribute("title")
    fun title(): String = "お薬基本情報更新"

    @ModelAttribute("executePath")
    fun executePath(@PathVariable medicineId: MedicineId): String = "/medicines/${medicineId}/basicinfo/update"

    /**
     * 薬基本情報更新画面を表示する
     */
    @GetMapping
    fun displayMedicineBasicInfoUpdatePage(@PathVariable medicineId: MedicineId,
                                           model: Model): String {
        if (!medicineQueryService.isValidMedicineId(medicineId)) throw InvalidEntityIdException(medicineId)

        val command = medicineUpdateService.getUpdateMedicineBasicInfoEditCommand(medicineId,
                                                                                  userSessionProvider.getUserSessionOrElseThrow())
        model.addAttribute("form", command)
        return "medicine/form"
    }

    /**
     * 薬基本情報を更新する
     */
    @PostMapping
    fun updateMedicineBasicInfo(@PathVariable medicineId: MedicineId,
                                @ModelAttribute("form") @Validated medicineBasicInfoEditCommand: MedicineBasicInfoEditCommand,
                                bindingResult: BindingResult,
                                redirectAttributes: RedirectAttributes): String {
        if (!medicineQueryService.isValidMedicineId(medicineId)) throw InvalidEntityIdException(medicineId)
        if (bindingResult.hasErrors()) return "medicine/form"

        medicineUpdateService.updateMedicineBasicInfo(medicineId,
                                                      medicineBasicInfoEditCommand,
                                                      userSessionProvider.getUserSessionOrElseThrow())
        redirectAttributes.addFlashAttribute("resultMessage",
                                             ResultMessage.info("お薬基本情報の更新が完了しました。"))
        redirectAttributes.addAttribute("medicineId", medicineId)
        return "redirect:/medicines/{medicineId}"
    }
}
