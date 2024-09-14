package example.presentation.controller.page.medicationrecord

import example.application.service.medicationrecord.*
import example.domain.model.medicationrecord.*
import example.domain.shared.message.*
import example.presentation.shared.session.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.validation.*
import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.*

@Controller
@RequestMapping("/medication-records/add")
class MedicationRecordAdditionController(private val medicationRecordService: MedicationRecordService,
                                         private val userSessionProvider: UserSessionProvider,
                                         private val lastRequestedPage: LastRequestedPage) {
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
    fun displayMedicationRecordAdditionPage(@Validated formInitialValues: MedicationRecordAdditionFormInitialValues,
                                            model: Model): String {
        val command =
                medicationRecordService.getAdditionMedicationRecordEditCommand(formInitialValues.validatedMedicine,
                                                                               formInitialValues.date,
                                                                               userSessionProvider.getUserSessionOrElseThrow())
        model.addAttribute("form", command)
        return "medicationrecord/form"
    }

    /**
     * 服用記録を追加する
     */
    @PostMapping
    fun addMedicationRecord(@ModelAttribute("form") @Validated medicationRecordEditCommand: MedicationRecordEditCommand,
                            bindingResult: BindingResult,
                            redirectAttributes: RedirectAttributes): String {
        if (bindingResult.hasErrors()) return "medicationrecord/form"

        medicationRecordService.addMedicationRecord(medicationRecordEditCommand,
                                                    userSessionProvider.getUserSessionOrElseThrow())
        redirectAttributes.addFlashAttribute("resultMessage",
                                             ResultMessage.info("服用記録の追加が完了しました。"))

        return "redirect:${lastRequestedPage.path}"
    }
}
