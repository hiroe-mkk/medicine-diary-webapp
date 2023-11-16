package example.presentation.controller.page.medicationrecord

import example.application.service.medicationrecord.*
import example.application.service.medicine.*
import example.domain.model.medicationrecord.*
import example.domain.model.medicine.*
import example.domain.shared.message.*
import example.presentation.shared.*
import example.presentation.shared.session.*
import example.presentation.shared.usersession.*
import jakarta.servlet.http.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.validation.*
import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.support.*
import org.springframework.web.servlet.mvc.support.*
import java.net.*

@Controller
@RequestMapping("/medication-records/add")
@SessionAttributes(value = ["lastRequestedPagePath"])
class MedicationRecordAdditionController(private val medicationRecordService: MedicationRecordService,
                                         private val userSessionProvider: UserSessionProvider) {
    @ModelAttribute("conditionLevels")
    fun conditionLevels(): Array<ConditionLevel> = ConditionLevel.values()

    @ModelAttribute("title")
    fun title(): String = "服用記録追加"

    @ModelAttribute("executePath")
    fun executePath(): String = "/medication-records/add"

    /**
     * 服用記録追加画面を表示する
     */
    @GetMapping
    fun displayMedicationRecordAdditionPage(@RequestParam(name = "medicine", required = false) medicineId: MedicineId?,
                                            lastRequestedPagePath: LastRequestedPagePath?,
                                            redirectAttributes: RedirectAttributes,
                                            model: Model): String {
        val command = medicationRecordService.getAdditionMedicationRecordEditCommand(medicineId,
                                                                                     userSessionProvider.getUserSession())
        model.addAttribute("form", command)
        return "medicationrecord/form"
    }

    /**
     * 服用記録を追加する
     */
    @PostMapping
    fun addMedicationRecord(@ModelAttribute("form") @Validated medicationRecordEditCommand: MedicationRecordEditCommand,
                            bindingResult: BindingResult,
                            redirectAttributes: RedirectAttributes,
                            lastRequestedPagePath: LastRequestedPagePath?): String {
        if (bindingResult.hasErrors()) return "medicationrecord/form"

        try {
            medicationRecordService.addMedicationRecord(medicationRecordEditCommand,
                                                        userSessionProvider.getUserSession())
            redirectAttributes.addFlashAttribute("resultMessage",
                                                 ResultMessage.info("服用記録の追加が完了しました。"))
        } catch (ex: MedicineNotFoundException) {
            redirectAttributes.addFlashAttribute("resultMessage",
                                                 ResultMessage.error("服用記録の追加に失敗しました。"))
        }

        val redirectPath = lastRequestedPagePath?.value ?: "/mypage"
        return "redirect:$redirectPath"
    }
}